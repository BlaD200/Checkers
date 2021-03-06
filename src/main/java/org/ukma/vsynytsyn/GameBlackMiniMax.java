package org.ukma.vsynytsyn;

import lombok.SneakyThrows;
import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.JoinStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.minimax.MiniMax;
import org.ukma.vsynytsyn.utils.Tuple;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

import static org.ukma.vsynytsyn.Game.MINIMAX_DEPTH;

public class GameBlackMiniMax implements Runnable {

    private final GameRequests black = new GameRequests();
    private final Semaphore lock;
    private final MiniMax miniMax;

    private boolean redPlayer;

    private long stepMillis;
    private long stepCount;


    public GameBlackMiniMax(Semaphore lock) {
        this.lock = lock;
        this.miniMax = new MiniMax();
    }


    @SneakyThrows
    public void run() {
        initBlackPlayer();

        System.out.print("BLACK move: ");
        while (true) {
            long start = System.currentTimeMillis();
            List<Cell> board = black.gameStatus().getData().getBoard();
            Tuple<Tuple<List<Cell>, String>, Double> move = miniMax.miniMax(
                    new Tuple<>(board, ""),
                    MINIMAX_DEPTH, redPlayer,
                    -100, 100
            );
            String moveVal = move.getFirst().getSecond();
            System.out.println(moveVal);
            move(moveVal);
            long end = System.currentTimeMillis();
            long timeForStep = end - start;
            System.out.println("Black time for move: " + timeForStep + "ms");
            stepMillis += timeForStep;
            ++stepCount;

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

        long l = stepMillis / stepCount;
        System.out.printf("\nBlack average time for move at depth %d is %d ms%n", MINIMAX_DEPTH, l);
    }


    private void initBlackPlayer() {
        try {
            Thread.sleep(250);
            JoinStatus joinStatus = black.joinGame("Team2");
            System.out.println("BLACK joins; " + joinStatus);
            System.out.println("BLACK data; " + black.gameStatus());
            redPlayer = joinStatus.getData().getColor() == PlayerColor.RED;
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
