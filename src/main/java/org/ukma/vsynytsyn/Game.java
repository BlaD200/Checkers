package org.ukma.vsynytsyn;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Game {

    private static AtomicBoolean redMoved = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        Thread red = new Thread(Game::redPlayer);
        Thread black = new Thread(Game::blackPlayer);

        red.start();
        black.start();

        red.join();
        black.join();
    }


    private static void redPlayer() {
        GameRequests red = new GameRequests();
        try {
            System.out.println("RED DATA; " + red.gameStatus());
            System.out.println("RED JOINS; " + red.joinGame("Team1"));
            System.out.println("RED MOVE; " + red.move(9, 13));
            redMoved.set(true);
            System.out.println("RED DATA; " + red.gameStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void blackPlayer() {
        GameRequests black = new GameRequests();
        try {
            Thread.sleep(500);
            System.out.println("BLACK DATA; " + black.gameStatus());
            System.out.println("BLACK JOINS; " + black.joinGame("Team2"));
            while (!redMoved.get())
                Thread.sleep(5);
            System.out.println("BLACK MOVE; " + black.move(21, 17));
            System.out.println("BLACK DATA; " + black.gameStatus());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
