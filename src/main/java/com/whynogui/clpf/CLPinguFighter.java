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
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Terminal terminal = null;
        TerminalPixelScreen screen = null;
        try {
            terminal = factory.createTerminal();
            Board board = new Board(terminal.getTerminalSize().getColumns(),terminal.getTerminalSize().getRows());
            screen = new TerminalPixelScreen(terminal, 18);
            screen.startScreen();
            while (true) {
                int readCharacters = 0;
                while (readCharacters <5) {
                KeyStroke keyStroke = terminal.pollInput();
                if (keyStroke != null) {
                    switch (keyStroke.getKeyCode()) {
                        case 13 -> {
                            //ENTER = iceblock2
                            board.eventP2 = "special";
                        }
                        case 37 -> {
                            //left2
                            board.eventP2 = "left";
                        }
                        case 38 -> {
                            //up2
                            board.eventP2 = "up";
                        }
                        case 39 -> {
                            //right2
                            board.eventP2 = "right";
                        }
                        case 40 -> {
                            //down2
                            board.eventP2 = "down";
                        }
                        case 32 -> {
                            //SPACE = iceblock1
                            board.eventP1 = "special";
                        }
                        case 65 -> {
                            //left1
                            board.eventP1 = "left";
                        }
                        case 87 -> {
                            //up1
                            board.eventP1 = "up";
                        }
                        case 68 -> {
                            //right1
                            board.eventP1 = "right";
                        }
                        case 83 -> {
                            //down1
                            board.eventP1 = "down";
                        }
                    }

                    if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
                        break;
                    }
                }
                }
                
                Player p1 = board.player1;
                Player p2 = board.player2;
                screen.drawSprite(p1.x,p1.y,p1.getSprite());
                screen.drawSprite(p2.x,p2.y,p2.getSprite());
                
                screen.refresh(Screen.RefreshType.COMPLETE);
                terminal.setCursorPosition(0, 0);
                screen.clear();
            }
        } catch (IOException ignore) {
            // IGNORE
        } finally {
            if (screen != null) {
                try {
                    screen.close();
                } catch (IOException ignore) {
                    // IGNORE
                }
            }
        }
    }

}
