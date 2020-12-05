package org.ukma.vsynytsyn;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Game {

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);

        Scanner scanner = new Scanner(System.in);
        GameRed gameRed = new GameRed(scanner, semaphore);
        GameBlack gameBlack = new GameBlack(scanner, semaphore);

        Thread red = new Thread(gameRed);
        Thread black = new Thread(gameBlack);

        red.start();
        black.start();

        red.join();
        black.join();
    }


//    private static void redPlayer() {
//        GameRequests red = new GameRequests();
//        try {
//            System.out.println("RED DATA; " + red.gameStatus());
//            System.out.println("RED JOINS; " + red.joinGame("Team1"));
//            System.out.println("RED MOVE; " + red.move(9, 13));
//            redMoved.set(true);
//            System.out.println("RED DATA; " + red.gameStatus());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    private static void blackPlayer() {
//        GameRequests black = new GameRequests();
//        try {
//            Thread.sleep(500);
//            System.out.println("BLACK DATA; " + black.gameStatus());
//            System.out.println("BLACK JOINS; " + black.joinGame("Team2"));
//            while (!redMoved.get())
//                Thread.sleep(5);
//            System.out.println("BLACK MOVE; " + black.move(21, 17));
//            System.out.println("BLACK DATA; " + black.gameStatus());
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}
