package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalSize;

import java.nio.ByteBuffer;

public class Ansi8PixelScreenBuffer extends ByteBufferPixelScreenBuffer {

    private static final byte[] PIXEL_PREFIX = {
            0x1b, 0x5b, 0x34, 0x38, 0x3b, 0x35, 0x3b  // => '\033[48;5;' = '\x1b[48;5;'
    };
    private static final int PIXEL_VALUE_SIZE = 3; // => '000' - '255'
    private static final byte[] PIXEL_SUFFIX = { 0x6d, 0x20 }; // => 'm ']
    private static final int PIXEL_FULL_SIZE = PIXEL_PREFIX.length + PIXEL_VALUE_SIZE + PIXEL_SUFFIX.length;
    private static final byte LINE_ENDING = (byte) 0x10;

    public Ansi8PixelScreenBuffer(TerminalSize size, int backgroundColor) {
        super(size, backgroundColor);
    }

    private Ansi8PixelScreenBuffer(TerminalSize size, ByteBuffer buffer) {
        super(size, buffer);
    }

    private static int decodeThreeDigitAsciiInt(byte[] asciiDigits) {
        return PixelScreenBuffer.asciiToDigit(asciiDigits[0]) * 100
                + PixelScreenBuffer.asciiToDigit(asciiDigits[1]) * 10
                + PixelScreenBuffer.asciiToDigit(asciiDigits[2]);
    }

    private static byte[] encodeThreeDigitAsciiInt(int number) {
        return new byte[] { PixelScreenBuffer.digitToAscii(number / 100),
                PixelScreenBuffer.digitToAscii((number % 100) / 10),
                PixelScreenBuffer.digitToAscii(number % 10) };
    }

    private static ByteBuffer createBuffer(int numColumns, int numRows, boolean useLineEndings, int color) {
        byte[] colorBytes = encodeThreeDigitAsciiInt(color);
        byte[] pixelBytes = new byte[] {
                PIXEL_PREFIX[0], PIXEL_PREFIX[1], PIXEL_PREFIX[2], PIXEL_PREFIX[3], PIXEL_PREFIX[4], PIXEL_PREFIX[5],
                PIXEL_PREFIX[6], colorBytes[0], colorBytes[1], colorBytes[2], PIXEL_SUFFIX[0], PIXEL_SUFFIX[1]
        };
        ByteBuffer result = ByteBuffer.allocate(numColumns * numRows * PIXEL_FULL_SIZE + (useLineEndings ? numRows : 0));
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                result.put(pixelBytes);
            }
            if (useLineEndings) result.put(LINE_ENDING);
        }
        return result;
    }

    public static Ansi8PixelScreenBuffer convertColorArrayToBuffer(int[][] colors) {
        ByteBuffer buffer = createBuffer(colors.length, colors[0].length, false, 0);
        for (int column = 0; column < colors.length; column++) {
            for (int row = 0; row < colors.length; row++) {
                buffer.put(flatten2dCoordinate(column, row, colors.length) * PIXEL_FULL_SIZE + PIXEL_PREFIX.length,
                        encodeThreeDigitAsciiInt(colors[column][row]));
            }
        }
        return new Ansi8PixelScreenBuffer(new TerminalSize(colors.length, colors[0].length), buffer);
    }

    @Override
    protected ByteBuffer createInitialBuffer() {
        return createBuffer(getSize().getColumns(), getSize().getRows(), true, getBackgroundColor());
    }

    @Override
    protected int getPixelByteSize() {
        return PIXEL_FULL_SIZE;
    }

    @Override
    protected int getLineEndingByteSize() {
        return 1;
    }

    @Override
    protected int decodeAsciiInt(byte[] asciiDigits) {
        return decodeThreeDigitAsciiInt(asciiDigits);
    }

    @Override
    protected byte[] encodeAsciiInt(int number) {
        return encodeThreeDigitAsciiInt(number);
    }

    @Override
    protected byte[] getColorBytes(int column, int row) {
        byte[] result = new byte[3];
        getBuffer().get(flattenPixelPosition(column, row) * PIXEL_FULL_SIZE + row + PIXEL_PREFIX.length, result);
        return result;
    }

    @Override
    protected void setColorBytes(int column, int row, byte[] colorBytes) {
        getBuffer().put(flattenPixelPosition(column, row) * PIXEL_FULL_SIZE + row + PIXEL_PREFIX.length, colorBytes);
    }

}
