package com.whynogui.clpf;

public class Board {
    private int width, height;
    private int groundLevel;
    Player player1, player2;
    String eventP1, eventP2;
    int cooldown;

    public Board (int width, int height) {
        this.width = width;
        this.height = height;
        groundLevel = height *7/8;
        int playerHeight = height/3;
        int playerWidth = width/6;
        player1 = new Player(width/6,groundLevel - playerHeight,playerWidth,playerHeight,true,width,height);
        player2 = new Player(width *5/6,groundLevel - playerHeight,playerWidth,playerHeight,false,width,height);
    }

    public void gameLoop () {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        eventP1 = ""; //TODO: W,A,S,D... in "LEFT","RIGHT" etc. übersetzen
        eventP2 = ""; //TODO: Pfeiltasten, Space... in "LEFT","RIGHT" etc. übersetzen

        player1.update(eventP1);
        player2.update(eventP2);
        if (player1.getX() > player2.getX()) {
            player1.facingRight = false;
            player2.facingRight = true;
        } else {
            player1.facingRight = true;
            player2.facingRight = false;
        }

        if (player1.getHitbox() != null && player1.getHitbox().intersects(player2.getHurtBox())) {
            if (player2.getState().equals("block")) {

            } else if (player2.getState().equals("invincible")) {
            } else {
                {
                    player2.health -= (player1.getState().equals("heavyPunch")) ? Player.HEAVY_PUNCH_DAMAGE : Player.LIGHT_PUNCH_DAMAGE;
                    player2.state = "invincible";
                    player2.cooldown = 10;
                    hitCooldown();
                }
            }
        }
        if (player2.getHitbox() != null && player2.getHitbox().intersects(player1.getHurtBox())) {
            if (player1.getState().equals("block")) {

            } else if (player1.getState().equals("invincible")) {

            }
            else {
                player1.health -= (player1.getState().equals("heavyPunch")) ? Player.HEAVY_PUNCH_DAMAGE : Player.LIGHT_PUNCH_DAMAGE;
                player1.state = "invincible";
                player1.cooldown = 10;
                hitCooldown();
            }
        }
        if (player1.getIceblock() != null && player1.getIceblock().intersects(player2.getHurtBox())) {
            player2.health -= Player.ICE_BLOCK_DAMAGE;
            player2.state = "neutral";
            player1.iceblock = null;
            hitCooldown();
        }
        if (player2.getIceblock() != null && player2.getIceblock().intersects(player1.getHurtBox())) {
            player1.health -= Player.ICE_BLOCK_DAMAGE;
            player1.state = "neutral";
            player2.iceblock = null;
            hitCooldown();
        }

        if (player1.health <= 0) {
            //TODO: Player 2 wins
        } else if (player2.health <= 0) {
            //TODO: Player 1 wins
        }
    }
    
    void hitCooldown () {
        cooldown = 5;
    }
}
