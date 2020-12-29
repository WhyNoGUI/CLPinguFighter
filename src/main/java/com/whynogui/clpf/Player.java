package com.whynogui.clpf;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Player extends Sprite {
    public static final int MAX_HEALTH = 100;
    public static final int HEAVY_PUNCH_DAMAGE = 8;
    public static final int LIGHT_PUNCH_DAMAGE = 3;
    public static final int ICE_BLOCK_DAMAGE = 6;
    public static final int UPPERCUT_DAMAGE = 10;
    int health;
    int cooldown;
    int boardWidth, boardHeight;
    private Rectangle hurtBox, hitbox;
    IceBlock iceblock;
    String state = "neutral";
    boolean facingRight;


    public Player (int x, int y, int width, int height, boolean facingRight, int boardWidth, int boardHeight) {
        super(x,y,width,height);
        health = MAX_HEALTH;
        this.facingRight = facingRight;
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        updateHurtBox();
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
                updateHurtBox();
                hitbox = null;
            }
        }
        else cooldown--;
        switch (state) {
            case "neutral","invincible" -> {
                //the invincible state gets cancelled if the player presses a key during its duration
                doAction(event);
            }
            case "jump" -> {
                if (cooldown >= 30)
                    y -= boardHeight/20;
                else if (cooldown < 10)
                    y += boardHeight/20;
                updateHurtBox();
            }
            case "crouch" -> {
                if (cooldown >= 30) {
                    y += boardHeight / 50;
                    height -= boardHeight / 50;
                }
                else if (cooldown < 10) {
                    y -= boardHeight / 50;
                    height += boardHeight / 50;
                }
                updateHurtBox();
            }
            case "heavyPunch" -> {
                if (facingRight) {
                    if (cooldown > 42) {
                        //Animation
                    } else if (cooldown == 42)
                        hitbox = new Rectangle(x + width, y + height / 5, width / 4, height * 2 / 5);
                    else if (cooldown == 41)
                        hitbox = new Rectangle(x + width, y + height / 5, width *2 / 4, height * 2 / 5);
                    else if (cooldown == 40)
                        hitbox = new Rectangle(x + width, y + height / 5, width * 3 / 2, height * 2 / 5);
                    else if (cooldown == 38)
                        hitbox = null;
                    else {
                        //Animation
                    }
                } else {
                    if (cooldown > 42) {
                        //Animation
                    } else if (cooldown == 42)
                        hitbox = new Rectangle(x - width / 4, y + height / 5, width / 4, height * 2 / 5);
                    else if (cooldown == 41)
                        hitbox = new Rectangle(x - width * 2 / 4, y + height / 5, width * 2 / 4, height * 2 / 5);
                    else if (cooldown == 40)
                        hitbox = new Rectangle(x - width * 3 / 2, y + height / 5, width * 3 / 2, height * 2 / 5);
                    else if (cooldown == 38)
                        hitbox = null;
                    else {
                        //Animation
                    }
                }
            }
            case "lightPunch" -> {
                if (facingRight) {
                    if (cooldown > 27) {
                        //Animation
                    } else if (cooldown == 27)
                        hitbox = new Rectangle(x + width, y + height / 5, width / 3, height * 2 / 5);
                    else if (cooldown == 26)
                        hitbox = new Rectangle(x + width, y + height / 5, width * 2 / 3, height * 2 / 5);
                    else if (cooldown == 25)
                        hitbox = new Rectangle(x + width, y + height / 5, width, height * 2 / 5);
                    else if (cooldown == 23)
                        hitbox = null;
                    else {
                        //Animation
                    }
                } else {
                    if (cooldown > 27) {
                        //Animation
                    } else if (cooldown == 27)
                        hitbox = new Rectangle(x - width / 3, y + height / 5, width / 3, height * 2 / 5);
                    else if (cooldown == 26)
                        hitbox = new Rectangle(x - width * 2 / 3, y + height / 5, width * 2 / 3, height * 2 / 5);
                    else if (cooldown == 25)
                        hitbox = new Rectangle(x - width, y + height / 5, width, height * 2 / 5);
                    else if (cooldown == 23)
                        hitbox = null;
                    else {
                        //Animation
                    }
                }
            }
            case "special" -> {
                if (cooldown == 1) {
                    iceblock = new IceBlock(x+width/3,y+width/2,width/3,width/3);
                    iceblock.setDx(boardWidth/100 * (facingRight ? 1 : -1));
                }
            }
            case "uppercut" -> {
                if (facingRight) {
                //jumping animation
                if (cooldown >= 30) {
                    y -= boardHeight / 20;
                    hitbox = new Rectangle(x + width, y + height / 5, width / 3, height * 2 / 3);
                    hurtBox = new Rectangle(x,y,0,0);
                }
                //falling animation
                else if (cooldown < 10) {
                    y += boardHeight / 20;
                    updateHurtBox();
                }
                } else {
                    //jumping animation
                if (cooldown >= 30) {
                    y -= boardHeight / 20;
                    hitbox = new Rectangle(x - width / 3, y + height / 5, width / 3, height * 2 / 3);
                    hurtBox = new Rectangle(x,y,0,0);
                }
                //falling animation
                else if (cooldown < 10) {
                    y += boardHeight / 20;
                    updateHurtBox();
                }
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
                updateHurtBox();
            }
            case "right" -> {
                x += boardWidth/120;
                updateHurtBox();
            }
            case "jump" -> {
                cooldown = 40;
                state = "jump";
            }
            case "crouch" -> {
                cooldown = 40;
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
                cooldown = 50;
                state = "special";
            }
            case "uppercut" -> {
                cooldown = 40;
                state = "uppercut";
            }
            default -> {

            }
        }
    }

    private void updateHurtBox () {
        hurtBox = new Rectangle(x,y,width,height);
    }
}
