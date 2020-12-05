package org.ukma.vsynytsyn;

import java.io.IOException;

public class Game {

    public static void main(String[] args) {
        GameRequests red = new GameRequests();
        GameRequests black = new GameRequests();

        try {
            System.out.println(red.gameStatus());
            System.out.println(red.joinGame("Team1"));
//            System.out.println(black.joinGame("Team2"));
            System.out.println(red.move(9, 13));
//            System.out.println(black.move(21, 17));
            System.out.println(red.gameStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
