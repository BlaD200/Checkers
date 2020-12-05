package org.ukma.vsynytsyn;

import lombok.SneakyThrows;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class GameRed implements Runnable {

    private final GameRequests red = new GameRequests();
    private final Scanner scanner;
    public final Semaphore lock;


    public GameRed(Scanner scanner, Semaphore lock) {
        this.scanner = scanner;
        this.lock = lock;
    }


    @SneakyThrows
    public void run() {
        initRedPlayer();

        lock.acquire();
        System.out.print("RED move: ");
        while (scanner.hasNextLine()) {
            String move = scanner.nextLine(); // p1,p2
            move(move);

            GameStatus gameStatus = red.gameStatus();
            System.out.println("RED data; " + gameStatus);
            if (gameStatus.getData().getWhoseTurn() == PlayerColor.RED)
                continue;
            else if (gameStatus.getData().isFinished()) {
                System.out.println(gameStatus.getData().getWinner());
                break;
            }

            lock.release();
            Thread.sleep(100);
            lock.acquire();
            System.out.print("RED move: ");
        }
    }


    private void initRedPlayer() {
        try {
            System.out.println("RED joins; " + red.joinGame("Team1"));
            System.out.println("RED data; " + red.gameStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void move(String move) {
        int from = Integer.parseInt(move.split(",")[0]);
        int to = Integer.parseInt(move.split(",")[1]);
        try {
            red.move(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
