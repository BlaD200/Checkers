package org.ukma.vsynytsyn;

import java.io.IOException;

public class Game {

    public static void main(String[] args) {
        GameRequests requests = new GameRequests();

        try {
            System.out.println(requests.gameStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
