package org.ukma.vsynytsyn;

import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.JoinStatus;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.minimax.MiniMax;
import org.ukma.vsynytsyn.utils.Tuple;

import java.io.IOException;
import java.util.List;

import static org.ukma.vsynytsyn.Game.MINIMAX_DEPTH;

public class GameMiniMax {

    public static final int MAX_CONNECTION_ATTEMPTS = 5;

    private final GameRequests gameRequests;
    private final String teamName;
    //    public final Semaphore lock;
    private final MiniMax miniMax;
    GameStatus gameStatus;
    private PlayerColor playerColor;
    private List<Cell> boardRaw;
    private boolean redPlayer;

    private long stepMillis;
    private long stepCount;
    private long stepMillisMax;


    public GameMiniMax(/*Semaphore lock*/String teamName) {
        //        this.lock = lock;
        this.miniMax = new MiniMax();
        gameRequests = new GameRequests();
        this.teamName = teamName;
    }


    public void runAI() throws IOException {
        initRedPlayer();

        int connectionAttempts = MAX_CONNECTION_ATTEMPTS;

        while (true) {
            try {
                gameStatus = gameRequests.gameStatus();
                if (gameStatus.getData().getWinner() != null) { // game is end
                    System.out.printf("Game finished. Winner: %s\n", gameStatus.getData().getWinner());
                    break;
                } else if (gameStatus.getData().getWhoseTurn() == playerColor) { // our turn
                    boardRaw = gameStatus.getData().getBoard();
                    connectionAttempts = MAX_CONNECTION_ATTEMPTS;
                } else {
                    Thread.sleep(10);
                    continue;
                }
            } catch (IOException e) {
                System.err.println("Something went wrong while requested data from server.");
                System.out.println(e.getMessage());
                if (--connectionAttempts == 0)
                    throw e;
                else {
                    System.out.printf("Trying reconnect... %d\n", connectionAttempts);
                    continue;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long start = System.currentTimeMillis();

            Cell[] board = MiniMax.preprocessBoard(boardRaw);
            Tuple<Tuple<Cell[], String>, Double> move = miniMax.miniMax(
                    new Tuple<>(board, ""),
                    MINIMAX_DEPTH, redPlayer,
                    Double.MIN_VALUE, Double.MAX_VALUE
            );

            String moveVal = move.getFirst().getSecond();
//            System.out.printf("%s move: %s\n", playerColor, moveVal);
            move(moveVal);

            long end = System.currentTimeMillis();
            long timeForStep = end - start;
            System.out.printf("%s time for move(%d): %sms\n", playerColor, stepCount, timeForStep);
            stepMillis += timeForStep;
            ++stepCount;
            stepMillisMax = Math.max(stepMillisMax, timeForStep);
        }

        long l = stepMillis / stepCount;
        System.out.printf("\n%s average time for move at depth %d is %d ms\n",
                playerColor, MINIMAX_DEPTH, l);
        System.out.printf("%s MAX time for move at depth %d is %d ms\n",
                playerColor, MINIMAX_DEPTH, stepMillisMax);
    }


    private void initRedPlayer() throws IOException {
        int connectionAttempts = MAX_CONNECTION_ATTEMPTS;

        while (connectionAttempts >= 0) {
            try {
                System.out.println("Joining the game...");
                JoinStatus joinStatus = gameRequests.joinGame(teamName);
                if (joinStatus == null || !joinStatus.getStatus().equals("success")) {
                    --connectionAttempts;
                    Thread.sleep(1000);
                    continue;
                }
                playerColor = joinStatus.getData().getColor();
                redPlayer = playerColor == PlayerColor.RED;
                System.out.printf("Joins as %s; %s\n", playerColor, joinStatus);
                return;
            } catch (IOException e) {
                System.err.println("Something went wrong while requested data from server.");
                System.out.println(e.getMessage());
                if (--connectionAttempts == 0)
                    throw e;
                else {
                    System.out.printf("Trying reconnect... %d\n", connectionAttempts);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (connectionAttempts == -1) {
            throw new RuntimeException("Connection to server failed while tried to join the game.");
        }
    }


    private void move(String move) {
        int from = Integer.parseInt(move.split(",")[0]);
        int to = Integer.parseInt(move.split(",")[1]);
        try {
            gameRequests.move(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
