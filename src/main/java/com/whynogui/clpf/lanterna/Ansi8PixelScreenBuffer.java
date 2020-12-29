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

    public Ansi8PixelScreenBuffer(TerminalSize size, int backgroundColor) {
        super(size, backgroundColor);
    }

    @Override
    protected ByteBuffer createInitialBuffer() {
        int numPixels = getSize().getRows() * getSize().getColumns();
        byte[] bgBytes = encodeAsciiInt(getBackgroundColor());
        byte[] initialPixelBytes = new byte[] {
                PIXEL_PREFIX[0], PIXEL_PREFIX[1], PIXEL_PREFIX[2], PIXEL_PREFIX[3], PIXEL_PREFIX[4], PIXEL_PREFIX[5],
                PIXEL_PREFIX[6], bgBytes[0], bgBytes[1], bgBytes[2], PIXEL_SUFFIX[0], PIXEL_SUFFIX[1]
        };
        ByteBuffer result = ByteBuffer.allocate(numPixels * getPixelByteSize() + getSize().getRows());
        for (int i = 0; i < numPixels; i++) {
            if (i != 0 && i % (getSize().getColumns()) == 0) result.put((byte) 0x10);
            result.put(i * initialPixelBytes.length, initialPixelBytes);
        }
        return result;
    }

    @Override
    protected int getPixelByteSize() {
        return PIXEL_FULL_SIZE;
    }

    @Override
    protected int decodeAsciiInt(byte[] asciiDigits) {
        return PixelScreenBuffer.asciiToDigit(asciiDigits[0]) * 100
                + PixelScreenBuffer.asciiToDigit(asciiDigits[1]) * 10
                + PixelScreenBuffer.asciiToDigit(asciiDigits[2]);
    }

    @Override
    protected byte[] encodeAsciiInt(int number) {
        return new byte[] { PixelScreenBuffer.digitToAscii(number / 100),
                PixelScreenBuffer.digitToAscii((number % 100) / 10),
                PixelScreenBuffer.digitToAscii(number % 10) };
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
