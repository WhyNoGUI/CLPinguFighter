package com.whynogui.clpf;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends Sprite {
    public static final int MAX_HEALTH = 100;
    public static final int HEAVY_PUNCH_DAMAGE = 8;
    public static final int LIGHT_PUNCH_DAMAGE = 3;
    public static final int ICE_BLOCK_DAMAGE = 6;
    int health;
    private int cooldown;
    int boardWidth, boardHeight;
    private Rectangle hurtBox, hitbox;
    private IceBlock iceblock;
    String state;
    boolean facingRight;


    public Player (int x, int y, int width, int height, boolean facingRight, int boardWidth, int boardHeight) {
        super(x,y,width,height);
        health = MAX_HEALTH;
        this.facingRight = facingRight;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        updateHitbox();
    }

    public Rectangle getHurtBox () {
        return hurtBox;
    }

    public Rectangle getHitbox () {
        return hitbox;
    }

    public Rectangle getIceblock () {
        return iceblock;
    }

    public String getState () {
        return state;
    }

    public void update (String event) {
        if (iceblock != null) {
            iceblock.move();
            if (iceblock.getX()< -iceblock.width)
                iceblock = null;
        }
        if (cooldown == 0) {
            if (!state.equals("neutral")) {
                state = "neutral";
                hitbox = new Rectangle(x, y, width, height);
                hurtBox = null;
            }
        }
        else cooldown--;
        switch (state) {
            case "neutral" -> {
                doAction(event);
            }
            case "jump" -> {
                if (cooldown > 60)
                    y += boardHeight/50;
                else if (cooldown < 20)
                    y -= boardHeight/50;
                updateHitbox();
            }
            case "crouch" -> {
                if (cooldown > 60)
                    y -= boardHeight/60;
                else if (cooldown < 20)
                    y += boardHeight/60;
                updateHitbox();
            }
            case "heavyPunch" -> {
                if (cooldown > 40) {
                    //Animation
                } else if (cooldown == 40)
                    hurtBox = new Rectangle(x+width,y+height/5,width*3/2,height*2/5);
                else if (cooldown == 39)
                    hurtBox = null;
                else {
                    //Animation
                }
            }
            case "lightPunch" -> {
                if (cooldown > 25) {
                    //Animation
                } else if (cooldown == 25)
                    hurtBox = new Rectangle(x+width,y+height/5,width,height*2/5);
                else if (cooldown == 24)
                    hurtBox = null;
                else {
                    //Animation
                }
            }
            default -> {
            }
        }
    }

    public void doAction (String event) {
        switch (event) {
            case "left" -> {
                x -= boardWidth/120;
                updateHitbox();
            }
            case "right" -> {
                x += boardWidth/120;
                updateHitbox();
            }
            case "jump" -> {
                cooldown = 80;
                state = "jump";
            }
            case "crouch" -> {
                cooldown = 80;
                state = "crouch";
            }
            case "heavyPunch" -> {
                cooldown = 100;
                state = "heavyPunch";
            }
            case "lightPunch" -> {
                cooldown = 35;
                state = "lightPunch";
            }
            case "block" -> {
                cooldown = 1;
                state = "block";
            }
            case "special" -> {
                iceblock = new IceBlock(x+width/3,y-width*2/3,width/3,width/3);
                iceblock.setDx(boardWidth/100 * (facingRight ? 1 : -1));
            }
            default -> {

            }
        }
    }

    private void updateHitbox () {
        hitbox = new Rectangle(x,y,width,height);
    }
}
