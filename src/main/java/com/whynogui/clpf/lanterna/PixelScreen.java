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

    TextColor.Indexed DEFAULT_COLOR = TextColor.Indexed.fromRGB(0, 0, 0);

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

    void setColor(int column, int row, TextColor.Indexed color);

    void setColor(TerminalPosition position, TextColor.Indexed color);

    TextColor.Indexed getFrontColor(int column, int row);

    TextColor.Indexed getFrontColor(TerminalPosition position);

    TextColor.Indexed getBackColor(int column, int row);

    TextColor.Indexed getBackColor(TerminalPosition position);

    void refresh() throws IOException;

    void refresh(Screen.RefreshType refreshType) throws IOException;

    TerminalSize doResizeIfNecessary();

}
