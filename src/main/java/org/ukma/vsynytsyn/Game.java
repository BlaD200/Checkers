package org.ukma.vsynytsyn;

import java.io.IOException;

public class Game {

    public static final int MINIMAX_DEPTH = 9;

    public static void main(String[] args) throws InterruptedException, IOException {
        GameMiniMax gameMiniMax = new GameMiniMax(Math.random() > .5 ? "Team1" : "Team2");
        for (int i = 0; i < 5; i++) {
            gameMiniMax.runAI();
            Thread.sleep(2000);
        }
    }
}
