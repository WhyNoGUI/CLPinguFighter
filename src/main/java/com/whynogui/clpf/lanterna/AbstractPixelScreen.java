package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.io.IOException;

public abstract class AbstractPixelScreen implements PixelScreen {

    private TerminalPosition cursorPosition;
    private WhiteSpaceScreenBuffer backBuffer;
    private WhiteSpaceScreenBuffer frontBuffer;
    private final TextColor.Indexed defaultColor;

    private TabBehaviour tabBehaviour;

    private TerminalSize terminalSize;

    private TerminalSize latestResizeRequest;

    public AbstractPixelScreen(TerminalSize initialSize) {
        this(initialSize, DEFAULT_COLOR);
    }

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public AbstractPixelScreen(TerminalSize initialSize, TextColor.Indexed defaultColor) {
        this.frontBuffer = new WhiteSpaceScreenBuffer(initialSize);
        this.backBuffer = new WhiteSpaceScreenBuffer(initialSize);
        this.defaultColor = defaultColor;
        this.cursorPosition = new TerminalPosition(0, 0);
        this.tabBehaviour = TabBehaviour.ALIGN_TO_COLUMN_4;
        this.terminalSize = initialSize;
        this.latestResizeRequest = null;
    }

    @Override
    public TerminalPosition getCursorPosition() {
        return cursorPosition;
    }

    @Override
    public void setCursorPosition(TerminalPosition position) {
        if(position == null) {
            this.cursorPosition = null;
            return;
        }
        if(position.getColumn() < 0) {
            position = position.withColumn(0);
        }
        if(position.getRow() < 0) {
            position = position.withRow(0);
        }
        if(position.getColumn() >= terminalSize.getColumns()) {
            position = position.withColumn(terminalSize.getColumns() - 1);
        }
        if(position.getRow() >= terminalSize.getRows()) {
            position = position.withRow(terminalSize.getRows() - 1);
        }
        this.cursorPosition = position;
    }

    @Override
    public void setTabBehaviour(TabBehaviour tabBehaviour) {
        if(tabBehaviour != null) {
            this.tabBehaviour = tabBehaviour;
        }
    }

    @Override
    public TabBehaviour getTabBehaviour() {
        return tabBehaviour;
    }

    @Override
    public void setColor(TerminalPosition position, TextColor.Indexed color) {
        setColor(position.getColumn(), position.getRow(), color);
    }

    @Override
    public synchronized void setColor(int column, int row, TextColor.Indexed color) {
        backBuffer.setColorAt(column, row, color);
    }

    @Override
    public synchronized TextColor.Indexed getFrontColor(TerminalPosition position) {
        return getFrontColor(position.getColumn(), position.getRow());
    }

    @Override
    public TextColor.Indexed getFrontColor(int column, int row) {
        return getColorFromBuffer(frontBuffer, column, row);
    }

    @Override
    public synchronized TextColor.Indexed getBackColor(TerminalPosition position) {
        return getBackColor(position.getColumn(), position.getRow());
    }

    @Override
    public TextColor.Indexed getBackColor(int column, int row) {
        return getColorFromBuffer(backBuffer, column, row);
    }

    @Override
    public void refresh() throws IOException {
        refresh(Screen.RefreshType.AUTOMATIC);
    }

    @Override
    public void close() throws IOException {
        stopScreen();
    }

    @Override
    public synchronized void clear() {
        backBuffer.setAll(defaultColor);
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize pendingResize = getAndClearPendingResize();
        if(pendingResize == null) {
            return null;
        }

        // TODO
        // backBuffer = backBuffer.resize(pendingResize);
        // frontBuffer = frontBuffer.resize(pendingResize);
        return pendingResize;
    }

    @Override
    public TerminalSize getTerminalSize() {
        return terminalSize;
    }

    protected WhiteSpaceScreenBuffer getFrontBuffer() {
        return frontBuffer;
    }

    protected WhiteSpaceScreenBuffer getBackBuffer() {
        return backBuffer;
    }

    private synchronized TerminalSize getAndClearPendingResize() {
        if(latestResizeRequest != null) {
            terminalSize = latestResizeRequest;
            latestResizeRequest = null;
            return terminalSize;
        }
        return null;
    }

    protected void addResizeRequest(TerminalSize newSize) {
        latestResizeRequest = newSize;
    }

    private TextColor.Indexed getColorFromBuffer(WhiteSpaceScreenBuffer buffer, int column, int row) {
        return buffer.getColorAt(column, row);
    }

    @Override
    public String toString() {
        return getBackBuffer().toString();
    }

}
