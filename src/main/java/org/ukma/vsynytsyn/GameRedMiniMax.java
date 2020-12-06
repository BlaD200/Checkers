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

public class GameRedMiniMax implements Runnable {

    private final GameRequests red = new GameRequests();
    public final Semaphore lock;
    private final MiniMax miniMax;

    private boolean redPlayer;


    public GameRedMiniMax(Semaphore lock) {
        this.lock = lock;
        this.miniMax = new MiniMax();
    }


    @SneakyThrows
    public void run() {
        initRedPlayer();

        lock.acquire();
        System.out.print("RED move: ");
        while (true) {
            List<Cell> boardRaw = red.gameStatus().getData().getBoard();
            Cell[] board = MiniMax.preprocessBoard(boardRaw);
            Tuple<Tuple<Cell[], String>, Double> move = miniMax.miniMax(new Tuple<>(board, ""),
                    9, redPlayer,
                    Double.MIN_VALUE, Double.MAX_VALUE);
            String moveVal = move.getFirst().getSecond();
            System.out.println(moveVal);
            move(moveVal);

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
            JoinStatus joinStatus = red.joinGame("Team1");
            System.out.println("RED joins; " + joinStatus);
            System.out.println("RED data; " + red.gameStatus());
            redPlayer = joinStatus.getData().getColor() == PlayerColor.RED;
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
