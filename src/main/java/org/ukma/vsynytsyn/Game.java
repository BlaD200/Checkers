package org.ukma.vsynytsyn;

import java.io.IOException;

public class Game {

    public static final int MINIMAX_DEPTH = 14;


    public static void main(String[] args) {
        GameMiniMax gameMiniMax = new GameMiniMax(Math.random() > .5 ? "Team1" : "Team2");
        try {
            gameMiniMax.runAI();
        } catch (RuntimeException | IOException e){
            System.out.println(e.getMessage());
        }
    }
}
