package com.whynogui.clpf;

import java.awt.*;

public class IceBlock extends Rectangle {
    int dx;

    public void move () {
        x += dx;
    }

    public IceBlock (int x, int y, int width, int height) {
        super(x,y,width,height);
    }

    public void setDx (int dx) {
        this.dx = dx;
    }
}
