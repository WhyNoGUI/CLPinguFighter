package com.whynogui.clpf;

import java.awt.*;

public class Board {
    private int width, height;
    private int groundLevel;
    private Rectangle health1, health2;
    Player player1, player2;
    String eventP1, eventP2;
    int cooldown;

    public Board (int width, int height) {
        this.width = width;
        this.height = height;
        groundLevel = height *7/8;
        int playerHeight = height/3;
        int playerWidth = width/6;
        player1 = new Player(width/6 - playerWidth / 2,groundLevel - playerHeight,playerWidth,playerHeight,true,width,height);
        player2 = new Player(width *5/6 - playerWidth / 2,groundLevel - playerHeight,playerWidth,playerHeight,false,width,height);
        eventP1 = "";
        eventP2 = "";
    }

    public void gameLoop () {
        if (cooldown > 0) {
            cooldown--;
            return;
        }
        player1.update(eventP1);
        player2.update(eventP2);
        eventP1 = "";
        eventP2 = "";

        player1.update(eventP1);
        player2.update(eventP2);
        if (player1.getX() > player2.getX()) {
            player1.facingRight = false;
            player2.facingRight = true;
        } else {
            player1.facingRight = true;
            player2.facingRight = false;
        }

        /*if (player1.getHitbox() != null && player1.getHitbox().intersects(player2.getHurtBox())) {
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
        }*/
        int currentHealth = player2.health;

        //Player 1 damage
        switch (player1.getState()) {
            case "heavyPunch" -> {
                if (player1.getHitbox() != null && player1.getHitbox().intersects(player2.getHurtBox())) {
                    if (player2.getState().equals("block")) {

                    } else if (player2.getState().equals("invincible")) {
                    } else {
                        {
                            player2.health -= Player.HEAVY_PUNCH_DAMAGE;
                            player2.state = "invincible";
                            player2.cooldown = 10;
                            hitCooldown();
                        }
                    }
                }
            }
            case "lightPunch" -> {
                if (player1.getHitbox() != null && player1.getHitbox().intersects(player2.getHurtBox())) {
                    if (player2.getState().equals("block")) {

                    } else if (player2.getState().equals("invincible")) {
                    } else {
                        {
                            player2.health -= Player.LIGHT_PUNCH_DAMAGE;
                            player2.state = "invincible";
                            player2.cooldown = 10;
                            hitCooldown();
                        }
                    }
                }
            }
            case "uppercut" -> {
                if (player1.getHitbox() != null && player1.getHitbox().intersects(player2.getHurtBox())) {
                    player2.health -= Player.UPPERCUT_DAMAGE;
                    player2.state = "invincible";
                    player2.cooldown = 10;
                    hitCooldown();
                    player1.state = "jump";
                }
            }
            case "special" -> {
                if (player1.getIceblock() != null && player1.getIceblock().intersects(player2.getHurtBox())) {
                    player2.health -= Player.ICE_BLOCK_DAMAGE;
                    player2.state = "invincible";
                    player2.cooldown = 10;
                    player1.iceblock = null;
                    hitCooldown();
                }
            }
        }

        updateHealth2(currentHealth);

        currentHealth = player1.health;
        //Player 2 damage
        switch (player2.getState()) {
            case "heavyPunch" -> {
                if (player2.getHitbox() != null && player2.getHitbox().intersects(player1.getHurtBox())) {
                    if (player1.getState().equals("block")) {

                    } else if (player1.getState().equals("invincible")) {
                    } else {
                        {
                            player1.health -= Player.HEAVY_PUNCH_DAMAGE;
                            player1.state = "invincible";
                            player1.cooldown = 10;
                            hitCooldown();
                        }
                    }
                }
            }
            case "lightPunch" -> {
                if (player2.getHitbox() != null && player2.getHitbox().intersects(player1.getHurtBox())) {
                    if (player1.getState().equals("block")) {

                    } else if (player1.getState().equals("invincible")) {
                    } else {
                        {
                            player1.health -= Player.LIGHT_PUNCH_DAMAGE;
                            player1.state = "invincible";
                            player1.cooldown = 10;
                            hitCooldown();
                        }
                    }
                }
            }
            case "uppercut" -> {
                if (player2.getHitbox() != null && player2.getHitbox().intersects(player1.getHurtBox())) {
                    player1.health -= Player.UPPERCUT_DAMAGE;
                    player1.state = "invincible";
                    player1.cooldown = 10;
                    hitCooldown();
                    player2.state = "jump";
                }
            }
        }

        updateHealth1(currentHealth);
        
        if (player1.getIceblock() != null && player1.getIceblock().intersects(player2.getHurtBox())) {
            player2.health -= Player.ICE_BLOCK_DAMAGE;
            player2.state = "invincible";
            player2.cooldown = 10;
            player1.iceblock = null;
            hitCooldown();
        }
        if (player2.getIceblock() != null && player2.getIceblock().intersects(player1.getHurtBox())) {
            player1.health -= Player.ICE_BLOCK_DAMAGE;
            player1.state = "invincible";
            player1.cooldown = 10;
            player2.iceblock = null;
            hitCooldown();
        }

        if (player1.health <= 0) {
            //TODO: Player 2 wins
        } else if (player2.health <= 0) {
            //TODO: Player 1 wins
        }
    }

    private void updateHealth1(int currentHealth) {
        if((currentHealth - currentHealth%10 - player1.health - player1.health%10) >= 1) {
            health1 = new Rectangle((int) Math.round(health1.getX() + (this.width/2) - (this.width/10)), 0, (int) Math.round(health1.getWidth() - (this.width/2) - (this.width/10)), this.height/15);
        }
    }

    private void updateHealth2(int currentHealth) {
        if((currentHealth - currentHealth%10 - player2.health - player2.health%10) >= 1) {
            health2 = new Rectangle((this.width/2) + (this.width/10), 0, (int) Math.round(health2.getWidth() - (this.width/2) - (this.width/10)), this.height/15);
        }
    }

    void hitCooldown () {
        cooldown = 10;
    }
}
