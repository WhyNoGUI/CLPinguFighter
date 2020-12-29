package com.whynogui.clpf;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.whynogui.clpf.lanterna.TerminalPixelScreen;

import java.io.IOException;

public class CLPinguFighter {

    public static void main(String[] args) {
        DefaultTerminalFactory factory = new DefaultTerminalFactory().setPreferTerminalEmulator(true);
        Terminal terminal = null;
        TerminalPixelScreen screen = null;
        try {
            terminal = factory.createTerminal();
            terminal.setCursorVisible(false);
            screen = new TerminalPixelScreen(terminal, 18);
            screen.startScreen();
            while (true) {
                KeyStroke keyStroke = terminal.pollInput();
                if (keyStroke != null) {
                    terminal.flush();

                    if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
                        break;
                    }
                }
                screen.refresh(Screen.RefreshType.COMPLETE);
                terminal.setCursorPosition(0, 0);
            }
        } catch (IOException ignore) {

        } finally {
            if (screen != null) {
                try {
                    screen.close();
                } catch (IOException ignore) {

                }
            }
        }
    }

}
