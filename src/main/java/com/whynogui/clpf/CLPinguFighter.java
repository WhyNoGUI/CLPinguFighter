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
                    readCharacters++;
                KeyStroke keyStroke = terminal.pollInput();
                if (keyStroke != null) {
                    KeyType keyType = keyStroke.getKeyType();
                    if (keyType == KeyType.Character) {
                        switch (keyType) {
                                case 'w'-> {
                                    board.eventP1 = "up";
                                }
                                case 'a'-> {
                                    board.eventP1 = "left";
                                }
                                case 's'-> {
                                    board.eventP1 = "down";
                                }
                                case 'd'-> {
                                    board.eventP1 = "right";
                                }
                                case ' '-> {
                                    board.eventP1 = "special";
                                }
                                case '+'-> {
                                    board.eventP1 = "heavyPunch";
                                }
                                case '-'-> {
                                    board.eventP1 = "lightPunch";
                                }
                                case 'h'-> {
                                    board.eventP1 = "heavyPunch";
                                }
                                case 'l'-> {
                                    board.eventP1 = "lightPunch";
                                }
                        }
                    } else {
                    switch (keyType) {
                        case KeyType.Enter -> {
                            //ENTER = iceblock2
                            board.eventP2 = "special";
                        }
                        case KeyType.ArrowLeft -> {
                            //left2
                            board.eventP2 = "left";
                        }
                        case KeyType.ArrowUp -> {
                            //up2
                            board.eventP2 = "up";
                        }
                        case KeyType.ArrowRight -> {
                            //right2
                            board.eventP2 = "right";
                        }
                        case KeyType.ArrowDown -> {
                            //down2
                            board.eventP2 = "down";
                        }
                    }
                    }

                    if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
                        break;
                    }
                }
                }
                
                Player p1 = board.player1;
                Player p2 = board.player2;
                if(p1.getSprite() != null) 
                screen.drawSprite(p1.x,p1.y,p1.getSprite());
                if(p2.getSprite() != null)
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
