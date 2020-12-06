package org.ukma.vsynytsyn;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class GamePlayer {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);

        Scanner scanner = new Scanner(System.in);
        GameRed gameRed = new GameRed(scanner, semaphore);
        //                GameBlack gameBlack = new GameBlack(scanner, semaphore);


        Thread red = new Thread(gameRed);
        //                Thread black = new Thread(gameBlack);

        red.start();
        //                black.start();
    }
}
