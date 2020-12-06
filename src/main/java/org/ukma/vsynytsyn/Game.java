package org.ukma.vsynytsyn;

import java.io.IOException;

public class Game {

    public static final int MINIMAX_DEPTH = 9;

    public static void main(String[] args) throws InterruptedException, IOException {
//        Semaphore semaphore = new Semaphore(1);
//
//        Scanner scanner = new Scanner(System.in);
//        GameMiniMax gameMiniMax = new GameMiniMax(/*semaphore*/"Team1");
//        GameBlackMiniMax gameBlackMiniMax = new GameBlackMiniMax(semaphore);
//
////        GameRed gameRed = new GameRed(scanner, semaphore);
////        GameBlack gameBlack = new GameBlack(scanner, semaphore);
//
//        Thread red = new Thread(gameMiniMax);
//        Thread black = new Thread(gameBlackMiniMax);
//
////        Thread red = new Thread(gameRed);
////        Thread black = new Thread(gameBlack);
//
//        red.start();
//        black.start();
//
//        red.join();
//        black.join();
        GameMiniMax gameMiniMax = new GameMiniMax(Math.random() > .5 ? "Team1" : "Team2");
        for (int i = 0; i < 5; i++) {
            gameMiniMax.runAI();
            Thread.sleep(2000);
        }
    }


    //    private static void redPlayer() {
    //        GameRequests isRed = new GameRequests();
    //        try {
    //            System.out.println("RED DATA; " + isRed.gameStatus());
    //            System.out.println("RED JOINS; " + isRed.joinGame("Team1"));
    //            System.out.println("RED MOVE; " + isRed.move(9, 13));
    //            redMoved.set(true);
    //            System.out.println("RED DATA; " + isRed.gameStatus());
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
