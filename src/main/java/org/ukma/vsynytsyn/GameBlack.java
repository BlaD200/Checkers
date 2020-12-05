package org.ukma.vsynytsyn;

import lombok.SneakyThrows;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;

import java.io.IOException;
import java.util.Scanner;

public class GameBlack implements Runnable {

    private final GameRequests black = new GameRequests();
    private final Object lock;


    public GameBlack(Object lock) {
        this.lock = lock;
    }


    @SneakyThrows
    public void run() {
        GameBlack gameBlack = new GameBlack(lock);
        gameBlack.initBlackPlayer();

        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("BLACK move: ");
            while (scanner.hasNext()) {
                String move = scanner.nextLine(); // p1,p2
                gameBlack.move(move);

                GameStatus gameStatus = black.gameStatus();
                System.out.println("RED data; " + gameStatus);
                if (gameStatus.getData().getWhoseTurn() == PlayerColor.RED)
                    continue;

                lock.notifyAll();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("BLACK move: ");
            }
        }


    }


    private void initBlackPlayer() {
        try {
            Thread.sleep(500);
            System.out.println("BLACK joins; " + black.joinGame("Team2"));
            System.out.println("BLACK data; " + black.gameStatus());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void move(String move) {
        int from = Integer.parseInt(move.split(",")[0]);
        int to = Integer.parseInt(move.split(",")[1]);
        try {
            black.move(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
