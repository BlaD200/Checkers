package org.ukma.vsynytsyn;

import lombok.SneakyThrows;
import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.minimax.MiniMax;
import org.ukma.vsynytsyn.utils.Tuple;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GameBlackMiniMax implements Runnable {

    private final GameRequests black = new GameRequests();
    private final Semaphore lock;
    private final MiniMax miniMax;


    public GameBlackMiniMax(Semaphore lock) {
        this.lock = lock;
        this.miniMax = new MiniMax();
    }


    @SneakyThrows
    public void run() {
        initBlackPlayer();

        lock.acquire();
        System.out.print("BLACK move: ");
        while (true) {
            List<Cell> board = black.gameStatus().getData().getBoard();
            Tuple<Tuple<List<Cell>, String>, Double> move = miniMax.miniMax(new Tuple<>(board, ""),
                    4, false,
                    Double.MIN_VALUE, Double.MAX_VALUE);
            String moveVal = move.getFirst().getSecond();
            System.out.println(moveVal);
            move(moveVal);

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
