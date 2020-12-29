package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

public interface PixelScreenBuffer {

    static int asciiToDigit(byte asciiDigit) {
        return asciiDigit - 0x30;
    }

    static byte digitToAscii(int digit) {
        return (byte) (digit + 0x30);
    }

    boolean isVeryDifferent(PixelScreenBuffer other, int threshold);

    TerminalSize getSize();

    int getBackgroundColor();

    default int getColorAt(TerminalPosition position) {
        if (position == null) {
            throw new IllegalArgumentException("Cannot call getColorAt(..) with null position");
        }
        return getColorAt(position.getColumn(), position.getRow());
    }

    public int getColorAt(int column, int row);

    default public void setColorAt(TerminalPosition position, int color){
        if (position == null) {
            throw new IllegalArgumentException("Cannot call setColorAt(..) with null position");
        }
        setColorAt(position.getColumn(), position.getRow(), color);
    }

    public void setColorAt(int column, int row, int color);

    public void setAll(int color);
    
    default public void copyTo(PixelScreenBuffer destination) {
        TerminalSize size = getSize();
        copyTo(
                destination,
                0,
                size.getRows(),
                0,
                size.getColumns(),
                0,
                0);
    }

    public void copyTo(
            PixelScreenBuffer destination,
            int startRowIndex,
            int rows,
            int startColumnIndex,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset);

    default public void copyFrom(
            PixelScreenBuffer source,
            int startRowIndex,
            int rows,
            int startColumnIndex,
            int columns,
            int destinationRowOffset,
            int destinationColumnOffset) {

        source.copyTo(
                this,
                startRowIndex,
                rows, startColumnIndex,
                columns, destinationRowOffset,
                destinationColumnOffset);
    }

    public void reset();

    @Override
    public String toString();

}
