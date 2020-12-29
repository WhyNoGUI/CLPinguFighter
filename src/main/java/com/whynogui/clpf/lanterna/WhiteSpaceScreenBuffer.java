package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import java.nio.ByteBuffer;


public class WhiteSpaceScreenBuffer {

    private static final byte[] PIXEL_PREFIX = {
            0x1b, 0x5b, 0x34, 0x38, 0x3b, 0x35, 0x3b  // => '\033[48;5;' = '\x1b[48;5;'
    };
    private static final int PIXEL_VALUE_SIZE = 3; // => '000' - '255'
    private static final byte[] PIXEL_SUFFIX = { 0x6d, 0x20 }; // => 'm ']
    private static final int PIXEL_FULL_SIZE = PIXEL_PREFIX.length + PIXEL_VALUE_SIZE + PIXEL_SUFFIX.length;

    private final TerminalSize size;
    private final ByteBuffer buffer;

    public WhiteSpaceScreenBuffer(TerminalSize size) {
        this.size = size;
        this.buffer = ByteBuffer.allocateDirect(size.getColumns() * (PIXEL_FULL_SIZE) * size.getRows());
    }

    private static int asciiToDigit(byte asciiDigit) {
        return asciiDigit - 0x30;
    }

    private static byte digitToAscii(int digit) {
        return (byte) (digit + 0x30);
    }

    private static int decodeAsciiInt(byte[] asciiDigits) {
        return asciiToDigit(asciiDigits[0]) * 100 + asciiToDigit(asciiDigits[1]) * 10 + asciiToDigit(asciiDigits[2]);
    }

    private static byte[] encodeAsciiInt(int number) {
        return new byte[] { digitToAscii(number / 100), digitToAscii((number % 100) / 10), digitToAscii(number % 10) };
    }

    private void setColorBytes(int index, byte[] colorBytes) {
        buffer.put(index * PIXEL_FULL_SIZE + PIXEL_PREFIX.length, colorBytes);
    }

    private byte[] getColorBytes(int index) {
        byte[] result = new byte[3];
        buffer.get(index * PIXEL_FULL_SIZE + PIXEL_PREFIX.length, result);
        return result;
    }

    private int flattenPixelPosition(int x, int y) {
        return y * size.getColumns() + x;
    }

    boolean isVeryDifferent(WhiteSpaceScreenBuffer other, int threshold) {
        if(!getSize().equals(other.getSize())) {
            throw new IllegalArgumentException("Can't compare WhiteSpaceScreenBuffers of different sizes");
        }
        int differences = 0;
        for(int y = 0; y < getSize().getRows(); y++) {
            for(int x = 0; x < getSize().getColumns(); x++) {
                if(!getColorAt(x, y).equals(other.getColorAt(x, y))) {
                    if(++differences >= threshold) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public TerminalSize getSize() {
        return size;
    }

    public TextColor.Indexed getColorAt(TerminalPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Cannot call WhiteSpaceScreenBuffer.getColorAt(..) with null position");
        }
        return getColorAt(position.getColumn(), position.getRow());
    }

    public TextColor.Indexed getColorAt(int column, int row) {
        if (column < 0 || row < 0 || row >= size.getRows() || column >= size.getColumns()) {
            return null;
        }

        return new TextColor.Indexed(decodeAsciiInt(getColorBytes(flattenPixelPosition(column, row))));
    }

    public void setColorAt(TerminalPosition position, TextColor.Indexed color) {
        if (position == null) {
            throw new IllegalArgumentException("Cannot call WhiteSpaceScreenBuffer.setColorAt(..) with null position");
        }
        setColorAt(position.getColumn(), position.getRow(), color);
    }

    public void setColorAt(int column, int row, TextColor.Indexed color) {
        if (color == null) {
            throw new IllegalArgumentException("Cannot call WhiteSpaceScreenBuffer.setColorAt(..) with null color");
        }
        if (column < 0 || row < 0 || row >= size.getRows() || column >= size.getColumns()) {
            return;
        }

        // TODO
        // setColorBytes(flattenPixelPosition(column, row), encodeAsciiInt(color.colorIndex));
    }

    public void setAll(TextColor.Indexed character) {
        if(character == null) {
            throw new IllegalArgumentException("Cannot call WhiteSpaceScreenBuffer.setAll(..) with null color");
        }

        // TODO
    }

    public void copyTo(WhiteSpaceScreenBuffer destination) {
       // copyTo(destination, 0, buffer.length, 0, buffer[0].length, 0, 0);
    }

    public void copyTo(
            WhiteSpaceScreenBuffer destination,
            int startRowIndex,
            int rows,
            int startColumnIndex,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset) {
        /*
        if(startColumnIndex < 0) {
            destinationColumnOffset += -startColumnIndex;
            columns += startColumnIndex;
            startColumnIndex = 0;
        }
        if(startRowIndex < 0) {
            destinationRowOffset += -startRowIndex;
            rows += startRowIndex;
            startRowIndex = 0;
        }

        if(destinationColumnOffset < 0) {
            startColumnIndex -= destinationColumnOffset;
            columns += destinationColumnOffset;
            destinationColumnOffset = 0;
        }
        if(destinationRowOffset < 0) {
            startRowIndex -= destinationRowOffset;
            rows += destinationRowOffset;
            destinationRowOffset = 0;
        }

        rows = Math.min(buffer.length - startRowIndex, rows);
        columns = rows>0 ? Math.min(buffer[0].length - startColumnIndex, columns) : 0;

        columns = Math.min(destination.getSize().getColumns() - destinationColumnOffset, columns);
        rows = Math.min(destination.getSize().getRows() - destinationRowOffset, rows);

        if(columns <= 0 || rows <= 0) {
            return;
        }

        TerminalSize destinationSize = destination.getSize();
        int targetRow = destinationRowOffset;
        for(int y = startRowIndex; y < startRowIndex + rows && targetRow < destinationSize.getRows(); y++) {
            System.arraycopy(buffer[y], startColumnIndex, destination.buffer[targetRow++], destinationColumnOffset, columns);
        } */
    }

    public void copyFrom(
            WhiteSpaceScreenBuffer source,
            int startRowIndex,
            int rows,
            int startColumnIndex,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset) {

        source.copyTo(this, startRowIndex, rows, startColumnIndex, columns, destinationRowOffset, destinationColumnOffset);
    }

    @Override
    public String toString() {
        return null; // TODO
    }
}
