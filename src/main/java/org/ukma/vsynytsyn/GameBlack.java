package org.ukma.vsynytsyn;

import lombok.SneakyThrows;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class GameBlack implements Runnable {

    private final GameRequests black = new GameRequests();
    private final Semaphore lock;
    private final Scanner scanner;


    public GameBlack(Scanner scanner, Semaphore lock) {
        this.lock = lock;
        this.scanner = scanner;
    }


    @SneakyThrows
    public void run() {
        initBlackPlayer();

        lock.acquire();
        System.out.print("BLACK move: ");
        while (scanner.hasNextLine()) {
            String move = scanner.nextLine(); // p1,p2
            move(move);

            GameStatus gameStatus = black.gameStatus();
            System.out.println("BLACK data; " + gameStatus);
            if (gameStatus.getData().getWhoseTurn() == PlayerColor.BLACK)
                continue;
            else if (gameStatus.getData().isFinished())
                break;

            lock.release();
            Thread.sleep(100);
            lock.acquire();
            System.out.print("BLACK move: ");
        }
    }


    private void initBlackPlayer() {
        try {
            Thread.sleep(250);
            System.out.println("BLACK joins; " + black.joinGame("Team2"));
            System.out.println("BLACK data; " + black.gameStatus());
            Thread.sleep(250);
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
