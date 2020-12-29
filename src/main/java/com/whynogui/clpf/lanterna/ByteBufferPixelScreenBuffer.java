package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

import java.nio.ByteBuffer;

public abstract class ByteBufferPixelScreenBuffer implements PixelScreenBuffer {

    private static final byte[] PIXEL_PREFIX = {
            0x1b, 0x5b, 0x34, 0x38, 0x3b, 0x35, 0x3b  // => '\033[48;5;' = '\x1b[48;5;'
    };
    private static final int PIXEL_VALUE_SIZE = 3; // => '000' - '255'
    private static final byte[] PIXEL_SUFFIX = { 0x6d, 0x20 }; // => 'm ']
    private static final int PIXEL_FULL_SIZE = PIXEL_PREFIX.length + PIXEL_VALUE_SIZE + PIXEL_SUFFIX.length;

    private final TerminalSize size;
    private final int backgroundColor;
    private final ByteBuffer initialBuffer;
    private final ByteBuffer buffer;

    public ByteBufferPixelScreenBuffer(TerminalSize size, int backgroundColor) {
        this.size = size;
        this.backgroundColor = backgroundColor;
        this.initialBuffer = createInitialBuffer();
        this.buffer = (initialBuffer.isDirect()) ?
                ByteBuffer.allocateDirect(initialBuffer.capacity()) :
                ByteBuffer.allocate(initialBuffer.capacity());
        this.buffer.put(initialBuffer.asReadOnlyBuffer()).flip();
    }

    public ByteBufferPixelScreenBuffer(TerminalSize size, ByteBuffer buffer) {
        this.size = size;
        this.backgroundColor = -1;
        this.initialBuffer = null;
        this.buffer = buffer;
    }

    private static ByteBuffer cloneByteBuffer(ByteBuffer original) {
        ByteBuffer clone = (original.isDirect()) ?
                ByteBuffer.allocateDirect(original.capacity()) :
                ByteBuffer.allocate(original.capacity());
        ByteBuffer readOnlyCopy = original.asReadOnlyBuffer();
        readOnlyCopy.flip();
        clone.put(readOnlyCopy);
        return clone;
    }

    protected abstract ByteBuffer createInitialBuffer();

    protected abstract int getPixelByteSize();

    protected abstract int getLineEndingByteSize();

    protected abstract int decodeAsciiInt(byte[] asciiDigits);

    protected abstract byte[] encodeAsciiInt(int number);

    protected abstract byte[] getColorBytes(int column, int row);

    protected abstract void setColorBytes(int column, int row, byte[] colorBytes);

    protected ByteBuffer getBuffer() {
        return buffer;
    }

    protected static int flatten2dCoordinate(int column, int row, int numColumns) {
        return row * numColumns + column;
    }

    protected int flattenPixelPosition(int column, int row) {
        return flatten2dCoordinate(column, row, getSize().getColumns());
    }

    @Override
    public boolean isVeryDifferent(PixelScreenBuffer other, int threshold) {
        if(!getSize().equals(other.getSize())) {
            throw new IllegalArgumentException("Can't compare WhiteSpaceScreenBuffers of different sizes");
        }
        int differences = 0;
        for(int y = 0; y < getSize().getRows(); y++) {
            for(int x = 0; x < getSize().getColumns(); x++) {
                if(getColorAt(x, y) != (other.getColorAt(x, y))) {
                    if(++differences >= threshold) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public TerminalSize getSize() {
        return size;
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public int getColorAt(TerminalPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Cannot call WhiteSpaceScreenBuffer.getColorAt(..) with null position");
        }
        return getColorAt(position.getColumn(), position.getRow());
    }

    @Override
    public int getColorAt(int column, int row) {
        if (column < 0 || row < 0 || row >= size.getRows() || column >= size.getColumns()) {
            return -1;
        }

        return decodeAsciiInt(getColorBytes(column, row));
    }

    @Override
    public void setColorAt(TerminalPosition position, int color) {
        if (position == null) {
            throw new IllegalArgumentException("Cannot call WhiteSpaceScreenBuffer.setColorAt(..) with null position");
        }
        setColorAt(position.getColumn(), position.getRow(), color);
    }

    @Override
    public void setColorAt(int column, int row, int color) {
        if (column < 0 || row < 0 || row >= size.getRows() || column >= size.getColumns()) {
            return;
        }

        setColorBytes(column, row, encodeAsciiInt(color));
    }

    @Override
    public void setAll(int color) {
        // TODO
    }

    @Override
    public void copyTo(
            PixelScreenBuffer destination,
            int sourceRowOffset,
            int rows,
            int sourceColumnOffset,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset) {

        if(sourceColumnOffset < 0) {
            destinationColumnOffset += -sourceColumnOffset;
            columns += sourceColumnOffset;
            sourceColumnOffset = 0;
        }
        if(sourceRowOffset < 0) {
            destinationRowOffset += -sourceRowOffset;
            rows += sourceRowOffset;
            sourceRowOffset = 0;
        }

        if(destinationColumnOffset < 0) {
            sourceColumnOffset -= destinationColumnOffset;
            columns += destinationColumnOffset;
            destinationColumnOffset = 0;
        }
        if(destinationRowOffset < 0) {
            sourceRowOffset -= destinationRowOffset;
            rows += destinationRowOffset;
            destinationRowOffset = 0;
        }

        rows = Math.min(size.getRows() - sourceRowOffset, rows);
        columns = rows > 0 ? Math.min(size.getColumns() - sourceColumnOffset, columns) : 0;

        columns = Math.min(destination.getSize().getColumns() - destinationColumnOffset, columns);
        rows = Math.min(destination.getSize().getRows() - destinationRowOffset, rows);

        if(columns <= 0 || rows <= 0) {
            return;
        }

        if (destination instanceof ByteBufferPixelScreenBuffer) {
            ByteBufferPixelScreenBuffer actualDestination = (ByteBufferPixelScreenBuffer) destination;

            if (this.getPixelByteSize() != actualDestination.getPixelByteSize()) {
                throw new IllegalArgumentException("Cannot copy between ByteBufferPixelScreenBuffers of different pixel sizes.");
            }

            for(int y = 0; y < rows; y++) {
                int sourceByteOffset = ((sourceRowOffset + y) * getSize().getColumns()
                        + sourceColumnOffset) * getPixelByteSize()
                        + sourceRowOffset * getLineEndingByteSize();
                int destinationByteOffset = ((destinationRowOffset + y) * actualDestination.getSize().getColumns()
                        + destinationColumnOffset) * actualDestination.getPixelByteSize()
                        + destinationRowOffset * actualDestination.getLineEndingByteSize();

                actualDestination.buffer.put(destinationByteOffset, buffer.array(), sourceByteOffset, columns);
            }
        } else {
            throw new UnsupportedOperationException("Can only copy a ByteBufferPixelScreenBuffer to a subclass");
        }
    }

    @Override
    public void reset() {
        this.buffer.flip().put(this.initialBuffer.asReadOnlyBuffer().flip());
    }

    public byte[] toByteArray() {
        return buffer.array();
    }

    @Override
    public String toString() {
        return null; // TODO
    }

}
