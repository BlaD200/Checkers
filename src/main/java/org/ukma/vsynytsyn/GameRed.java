package org.ukma.vsynytsyn;

import lombok.SneakyThrows;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;

import java.io.IOException;
import java.util.Scanner;

public class GameRed implements Runnable {
    public final Object lock;
    private final GameRequests red = new GameRequests();


    public GameRed(Object lock) {
        this.lock = lock;
    }


    @SneakyThrows
    public void run() {
        GameRed gameRed = new GameRed(lock);
        gameRed.initRedPlayer();

        synchronized (lock) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("RED move: ");
            while (scanner.hasNext()) {
                String move = scanner.nextLine(); // p1,p2
                gameRed.move(move);

                GameStatus gameStatus = red.gameStatus();
                System.out.println("RED data; " + gameStatus);
                if (gameStatus.getData().getWhoseTurn() == PlayerColor.RED)
                    continue;

                lock.notifyAll();
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
