package com.whynogui.clpf;

import processing.core.PApplet;

import java.awt.*;

public class CLPinguFighterApplet extends PApplet {

    Board board;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 150;

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        board = new Board(WIDTH, HEIGHT);
    }

    @Override
    public void draw() {
        fill(255);
        rect(0,0, WIDTH, HEIGHT);

        //draw p1
        if (board.player1.health <= 0)
            fill(Color.red.getRGB());
        else if (board.player1.state != null && board.player1.state.equals("block"))
            fill(Color.blue.getRGB());
        else
            fill(0);
        rect(board.player1.x, board.player1.y, board.player1.width, board.player1.height);

        //draw p2
        if (board.player2.health <= 0)
            fill(Color.red.getRGB());
        else if (board.player2.state != null && board.player2.state.equals("block"))
            fill(Color.blue.getRGB());
        else
            fill(0);
        rect(board.player2.x, board.player2.y, board.player2.width, board.player2.height);

        //draw direction
        fill(Color.lightGray.getRGB());
        if (board.player1.facingRight) {
            triangle(board.player1.x, board.player1.y,
                    board.player1.x + board.player1.width, board.player1.y + (float)board.player1.height / 2,
                    board.player1.x, board.player1.y + board.player1.height);
        }else {
            triangle(board.player1.x + board.player1.width, board.player1.y,
                    board.player1.x + board.player1.width, board.player1.y + board.player1.height,
                    board.player1.x, board.player1.y + (float)board.player1.height/2);
        }

        if (board.player2.facingRight) {
            triangle(board.player2.x, board.player2.y,
                    board.player2.x + board.player2.width, board.player2.y + (float)board.player2.height / 2,
                    board.player2.x, board.player2.y + board.player2.height);
        }else {
            triangle(board.player2.x + board.player2.width, board.player2.y,
                    board.player2.x + board.player2.width, board.player2.y + board.player2.height,
                    board.player2.x, board.player2.y + (float)board.player2.height/2);
        }

        //do board update
        board.gameLoop();

        //draw hurt box p1
        fill(Color.red.getRGB());
        if (board.player1.getHitbox() != null) {
            Rectangle r = board.player1.getHitbox();
            rect(r.x, r.y, r.width, r.height);
        }

        //draw hurt box p2
        if (board.player2.getHitbox() != null) {
            Rectangle r = board.player2.getHitbox();
            rect(r.x, r.y, r.width, r.height);
        }

        //draw ice block p1
        fill(Color.CYAN.getRGB());
        if (board.player1.getIceblock() != null) {
            Rectangle r = board.player1.getIceblock();
            rect(r.x, r.y, r.width, r.height);
        }

        //draw ice block p2
        if (board.player2.getIceblock() != null) {
            Rectangle r = board.player2.getIceblock();
            rect(r.x, r.y, r.width, r.height);
        }

    }

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == LEFT) {
                board.eventP2 = "left";
            }
            if (keyCode == RIGHT) {
                board.eventP2 = "right";
            }
            if (keyCode == UP) {
                board.eventP2 = "jump";
            }
            if (keyCode == DOWN) {
                board.eventP2 = "crouch";
            }
        }

        if (key == 'a') {
            board.eventP1 = "left";
        }
        if (key == 'd') {
            board.eventP1 = "right";
        }
        if (key == 'w') {
            board.eventP1 = "jump";
        }
        if (key == 's') {
            board.eventP1 = "crouch";
        }

        if (key == 'r') {
            board.eventP1 = "heavyPunch";
        }
        if (key == 'f') {
            board.eventP1 = "lightPunch";
        }

        if (key == 'p') {
            board.eventP2 = "heavyPunch";
        }
        if (key == 'l') {
            board.eventP2 = "lightPunch";
        }

        if (key == 'c') {
            board.eventP1 = "block";
        }

        if (key == 'o') {
            board.eventP2 = "block";
        }

        if (key == 'q') {
            board.eventP1 = "special";
        }
        if (key == 'k') {
            board.eventP2 = "special";
        }

    }

}
