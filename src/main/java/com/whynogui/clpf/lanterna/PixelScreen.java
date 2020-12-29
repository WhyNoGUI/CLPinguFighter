package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.InputProvider;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;

import java.io.Closeable;
import java.io.IOException;

public interface PixelScreen extends InputProvider, Closeable {

    int DEFAULT_BACKGROUND_COLOR = 0;

    void startScreen() throws IOException;

    @Override
    void close() throws IOException;

    void stopScreen() throws IOException;

    void clear();

    TerminalPosition getCursorPosition();

    void setCursorPosition(TerminalPosition position);

    TabBehaviour getTabBehaviour();

    void setTabBehaviour(TabBehaviour tabBehaviour);

    TerminalSize getTerminalSize();

    int getBackgroundColor();

    void setColor(int column, int row, int color);

    void setColor(TerminalPosition position, int color);

    int getFrontColor(int column, int row);

    int getFrontColor(TerminalPosition position);

    int getBackColor(int column, int row);

    int getBackColor(TerminalPosition position);

    void refresh() throws IOException;

    void refresh(Screen.RefreshType refreshType) throws IOException;

    TerminalSize doResizeIfNecessary();

}
