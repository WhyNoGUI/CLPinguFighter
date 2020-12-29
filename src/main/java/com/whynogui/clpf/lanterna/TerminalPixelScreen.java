package com.whynogui.clpf.lanterna;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class TerminalPixelScreen extends AbstractPixelScreen {

    private final Terminal terminal;
    private boolean isStarted;
    private boolean fullRedrawHint;

    public TerminalPixelScreen(Terminal terminal) throws IOException {
        this(terminal, DEFAULT_BACKGROUND_COLOR);
    }

    public TerminalPixelScreen(Terminal terminal, int backgroundColor) throws IOException {
        super(terminal.getTerminalSize(), backgroundColor);
        this.terminal = terminal;
        this.terminal.addResizeListener(new TerminalPixelScreenResizeListener());
        this.isStarted = false;
        this.fullRedrawHint = true;
    }

    @Override
    public synchronized void startScreen() throws IOException {
        if(isStarted) {
            return;
        }

        isStarted = true;
        getTerminal().enterPrivateMode();
        getTerminal().getTerminalSize();
        getTerminal().clearScreen();
        this.fullRedrawHint = true;
        TerminalPosition cursorPosition = getCursorPosition();
        if(cursorPosition != null) {
            getTerminal().setCursorVisible(true);
            getTerminal().setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
        } else {
            getTerminal().setCursorVisible(false);
        }
    }

    @Override
    public void stopScreen() throws IOException {
        stopScreen(true);
    }

    public synchronized void stopScreen(boolean flushInput) throws IOException {
        if(!isStarted) {
            return;
        }

        if (flushInput) {
            //Drain the input queue
            KeyStroke keyStroke;
            do {
                keyStroke = pollInput();
            }
            while(keyStroke != null && keyStroke.getKeyType() != KeyType.EOF);
        }

        getTerminal().exitPrivateMode();
        isStarted = false;
    }

    @Override
    public synchronized void refresh(Screen.RefreshType refreshType) throws IOException {
        if(!isStarted) {
            return;
        }
        if((refreshType == Screen.RefreshType.AUTOMATIC && fullRedrawHint) || refreshType == Screen.RefreshType.COMPLETE) {
            refreshFull();
            fullRedrawHint = false;
        }
        else if(refreshType == Screen.RefreshType.AUTOMATIC) {
            double threshold = getTerminalSize().getRows() * getTerminalSize().getColumns() * 0.75;
            if(getBackBuffer().isVeryDifferent(getFrontBuffer(), (int) threshold)) {
                refreshFull();
            }
            else {
                refreshByDelta();
            }
        }
        else {
            refreshByDelta();
        }
        getBackBuffer().copyTo(getFrontBuffer());
        TerminalPosition cursorPosition = getCursorPosition();
        if(cursorPosition != null) {
            getTerminal().setCursorVisible(true);
            getTerminal().setCursorPosition(cursorPosition.getColumn(), cursorPosition.getRow());
        } else {
            getTerminal().setCursorVisible(false);
        }
        getTerminal().flush();
    }

    private void refreshByDelta() throws IOException {
        Map<TerminalPosition, TextColor.Indexed> updateMap = new TreeMap<>(new ScreenPointComparator());
        TerminalSize terminalSize = getTerminalSize();

        for(int y = 0; y < terminalSize.getRows(); y++) {
            for(int x = 0; x < terminalSize.getColumns(); x++) {
                int backBufferColor = getBackBuffer().getColorAt(x, y);
                int frontBufferColor = getFrontBuffer().getColorAt(x, y);
                if(backBufferColor != frontBufferColor) {
                    updateMap.put(new TerminalPosition(x, y), new TextColor.Indexed(backBufferColor));
                }
            }
        }

        if(updateMap.isEmpty()) {
            return;
        }

        TerminalPosition currentPosition = updateMap.keySet().iterator().next();
        getTerminal().setCursorPosition(currentPosition.getColumn(), currentPosition.getRow());

        TextColor.Indexed currentColor = updateMap.values().iterator().next();
        getTerminal().setBackgroundColor(currentColor);
        for(TerminalPosition position: updateMap.keySet()) {
            if(!position.equals(currentPosition)) {
                getTerminal().setCursorPosition(position.getColumn(), position.getRow());
                currentPosition = position;
            }
            TextColor.Indexed newColor = updateMap.get(position);
            if(!currentColor.equals(newColor)) {
                getTerminal().setBackgroundColor(newColor);
                currentColor = newColor;
            }
            getTerminal().putCharacter(' ');
            currentPosition = currentPosition.withRelativeColumn(1);
        }
    }

    private void refreshFull() {
        System.out.writeBytes(getBackBuffer().toByteArray());
        System.out.flush();
    }

    @SuppressWarnings("WeakerAccess")
    public Terminal getTerminal() {
        return terminal;
    }

    @Override
    public KeyStroke readInput() throws IOException {
        return terminal.readInput();
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        return terminal.pollInput();
    }

    @Override
    public synchronized void clear() {
        super.clear();
        fullRedrawHint = true;
    }

    @Override
    public synchronized TerminalSize doResizeIfNecessary() {
        TerminalSize newSize = super.doResizeIfNecessary();
        if(newSize != null) {
            fullRedrawHint = true;
        }
        return newSize;
    }

    private class TerminalPixelScreenResizeListener implements TerminalResizeListener {
        @Override
        public void onResized(Terminal terminal, TerminalSize newSize) {
            addResizeRequest(newSize);
        }
    }

    private static class ScreenPointComparator implements Comparator<TerminalPosition> {
        @Override
        public int compare(TerminalPosition o1, TerminalPosition o2) {
            if(o1.getRow() == o2.getRow()) {
                if(o1.getColumn() == o2.getColumn()) {
                    return 0;
                } else {
                    return Integer.compare(o1.getColumn(), o2.getColumn());
                }
            } else {
                return Integer.compare(o1.getRow(), o2.getRow());
            }
        }
    }

}
