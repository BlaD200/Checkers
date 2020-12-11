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
    private final MiniMax miniMax;
    private int minimaxDepth;
    private PlayerColor playerColor;
    private List<Cell> board;
    private boolean redPlayer;

    private long stepMillis;
    private long stepCount;
    private long stepMillisMax;


    public GameMiniMax(String teamName) {
        this.gameRequests = new GameRequests();
        this.teamName = teamName;
        this.miniMax = new MiniMax();
        this.minimaxDepth = MINIMAX_DEPTH;
        miniMax.time = 300 * 1000;
        miniMax.resetTime();
    }


    public void runAI() throws IOException {
        initRedPlayer();

        int connectionAttempts = MAX_CONNECTION_ATTEMPTS;

        while (true) {
            long startStart = System.currentTimeMillis();
            try {
                GameStatus gameStatus = gameRequests.gameStatus();
                if ((gameStatus.getData().getWinner() != null &&
                        !gameStatus.getData().getStatus().equals("Game is playing"))
                        || gameStatus.getData().getStatus().equals("Game is over")
                ) { // game is end
                    System.out.printf("\nGame finished. Winner: %s\n", gameStatus.getData().getWinner());
                    System.out.println(gameStatus.getData());
                    break;
                } else if (gameStatus.getData().getWhoseTurn() == playerColor) { // our turn
                    board = gameStatus.getData().getBoard();
                    connectionAttempts = MAX_CONNECTION_ATTEMPTS;
                } else {
                    Thread.sleep(100);
                    continue;
                }
            } catch (IOException e) {
                System.err.println("\nSomething went wrong while requested data from server.");
                System.out.println(e.getMessage());
                if (--connectionAttempts == 0)
                    throw e;
                else {
                    System.out.printf("\nTrying reconnect... %d\n", connectionAttempts);
                    continue;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long start = System.currentTimeMillis();

            miniMax.resetTime();
            Tuple<Tuple<List<Cell>, String>, Double> move = miniMax.miniMax(
                    new Tuple<>(board, ""),
                    minimaxDepth, redPlayer,
                    -100, 100
            );

            String moveVal = move.getFirst().getSecond();
            String[] moves = moveVal.trim().split(" ");
            for (String s : moves) {
                //                System.out.printf("%s move: %s\n", playerColor, moveVal);
                long beforeMove = System.currentTimeMillis();
                move(s);
                long afterMove = System.currentTimeMillis();
                long moveTime = afterMove - beforeMove;

                long end = System.currentTimeMillis();
                long timeForStep = end - start;
                System.out.printf("\n%s time for move(%d): %dms (a) | %dms (m) ", playerColor, stepCount, timeForStep,
                        moveTime);
                stepMillis += timeForStep;
                ++stepCount;
                stepMillisMax = Math.max(stepMillisMax, timeForStep);
            }

            long endEnd = System.currentTimeMillis();
            long timeForStep = endEnd - startStart;
            System.out.printf("| %dms (f)", timeForStep);
            //            stepMillis += timeForStep;
            //            ++stepCount;
            //            stepMillisMax = Math.max(stepMillisMax, timeForStep);
        }

        long l = stepMillis / stepCount;
        System.out.printf("\n%s average time for move at depth %d is %d ms\n",
                playerColor, minimaxDepth, l);
        System.out.printf("%s MAX time for move at depth %d is %d ms\n",
                playerColor, minimaxDepth, stepMillisMax);
    }


    private void initRedPlayer() throws IOException {
        int connectionAttempts = MAX_CONNECTION_ATTEMPTS;

        while (connectionAttempts >= 0) {
            try {
                GameStatus gameStatus = gameRequests.gameStatus();
                if (gameStatus != null)
                    miniMax.time = (long) (gameStatus.getData().getAvailableTime() * 1000) - 300;

                System.out.println("Joining the game...");
                JoinStatus joinStatus = gameRequests.joinGame(teamName);
                if (joinStatus == null || !joinStatus.getStatus().equals("success")) {
                    --connectionAttempts;
                    Thread.sleep(5000);
                    continue;
                }
                minimaxDepth = miniMax.time < 1000 ? 10 : MINIMAX_DEPTH;
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
