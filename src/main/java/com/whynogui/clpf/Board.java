package com.whynogui.clpf;

public class Board {
    private int width, height;
    private int groundLevel;
    Player player1, player2;

    public Board (int width, int height) {
        this.width = width;
        this.height = height;
        groundLevel = height *7/8;
        int playerHeight = height/3;
        int playerWidth = width/6;
        player1 = new Player(width/6,groundLevel - playerHeight,playerWidth,playerHeight,true,width,height);
        player2 = new Player(width *5/6,groundLevel - playerHeight,playerWidth,playerHeight,false,width,height);
    }

    public void checkCollisions () {
        String eventP1 = ""; //TODO: W,A,S,D... in "LEFT","RIGHT" etc. übersetzen
        String eventP2 = ""; //TODO: Pfeiltasten, Space... in "LEFT","RIGHT" etc. übersetzen

        player1.update(eventP1);
        player2.update(eventP2);

        if (player1.getHurtBox() != null && player1.getHurtBox().intersects(player2.getHitbox())) {
            if (player1.getState().equals("heavyPunch")) {
                player2.health -= Player.HEAVY_PUNCH_DAMAGE;
            } else if (!player2.getState().equals("block")) {
                player2.health -= Player.LIGHT_PUNCH_DAMAGE;
            }
            player2.state = "neutral";
        }
        if (player2.getHurtBox() != null && player2.getHurtBox().intersects(player1.getHitbox())) {
            if (player2.getState().equals("heavyPunch")) {
                player1.health -= Player.HEAVY_PUNCH_DAMAGE;
            } else if (!player1.getState().equals("block")) {
                player1.health -= Player.LIGHT_PUNCH_DAMAGE;
            }
            player2.state = "neutral";
        }
        if (player1.getIceblock() != null && player1.getIceblock().intersects(player2.getHitbox())) {
            player2.health -= Player.ICE_BLOCK_DAMAGE;
            player2.state = "neutral";
        }
        if (player2.getIceblock() != null && player2.getIceblock().intersects(player1.getHitbox())) {
            player1.health -= Player.ICE_BLOCK_DAMAGE;
            player1.state = "neutral";
        }
    }
}
