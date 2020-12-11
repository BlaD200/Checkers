package org.ukma.vsynytsyn.minimax;

import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.utils.Tuple;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class MiniMax {

    public static final int MIN_ROW = 0;
    public static final int MAX_ROW = 7;
    public static final int MIN_COL = 0;
    public static final int MAX_COL = 3;

//    private final ExecutorService executorService;
//    private int availableThreads = 4;

    public long time;
    private Instant start;


//    public MiniMax() {
//        executorService = Executors.newFixedThreadPool(availableThreads);
//    }


//    @Override
//    protected void finalize() throws Throwable {
//        super.finalize();
//        executorService.shutdown();
//        try {
//            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
//                executorService.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executorService.shutdownNow();
//        }
//    }


    public Tuple<Tuple<List<Cell>, String>, Double> miniMax(
            Tuple<List<Cell>, String> board, int depth,
            boolean maximizingPlayer, double alpha, double beta
    ) {
        if (depth == 0 || isTimeOut())
            return new Tuple<>(board, evaluateBoard(board.getFirst()));

        double eval = maximizingPlayer ? -100 : 100;

        List<Tuple<List<Cell>, String>> possibleBoardStates = getPossibleBoardStates(board.getFirst(), maximizingPlayer);
        if (possibleBoardStates.size() == 0)
            if (gameOver(board.getFirst()))
                return new Tuple<>(board, (maximizingPlayer ? 100. : -100.));
            else // checkers are blocked
                return new Tuple<>(board, (maximizingPlayer ? 0. : 0.));

        Tuple<List<Cell>, String> nextBoard = possibleBoardStates.get(0);

        for (Tuple<List<Cell>, String> state : possibleBoardStates) {
            if (isTimeOut())
                break;

            Tuple<Tuple<List<Cell>, String>, Double> boardEval = miniMax(state, depth - 1, !maximizingPlayer, alpha, beta);
            double newEval = boardEval.getSecond();

            if (maximizingPlayer) {
                if (newEval > eval) {
                    eval = newEval;
                    nextBoard = state;
                    alpha = newEval;
                }
            } else {
                if (newEval < eval) {
                    eval = newEval;
                    nextBoard = state;
                    beta = newEval;
                }
            }

            if (beta <= alpha)
                break;
        }

        return new Tuple<>(nextBoard, eval);
    }


    private List<Tuple<List<Cell>, String>> getPossibleBoardStates(List<Cell> boardState, boolean red) {
        List<Tuple<List<Cell>, String>> possibleBoardStates = new LinkedList<>();
        AtomicBoolean capture = new AtomicBoolean(false);

        for (Cell cell : boardState) {
            if (isTimeOut())
                break;
            if (cell.isRed() && red)
                findMove(boardState, possibleBoardStates, capture, cell, true, "");
            else if (!cell.isRed() && !red)
                findMove(boardState, possibleBoardStates, capture, cell, false, "");
        }

        return possibleBoardStates;
    }


    private void findMove(List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates,
                          final AtomicBoolean capture, Cell cell, boolean isRed, String currentMoves) {
        Boolean[] auxBoard = new Boolean[33]; // True - RED, False - Black, Null - Empty
        for (Cell c : boardState)
            auxBoard[c.getPosition()] = c.isRed();

        if (cell.isKing())
            if (isRed)
                findMovesLeft(boardState, possibleBoardStates, auxBoard, capture, cell, true, currentMoves);
            else
                findMovesRight(boardState, possibleBoardStates, auxBoard, capture, cell, false, currentMoves);
        if (isRed)
            findMovesRight(boardState, possibleBoardStates, auxBoard, capture, cell, true, currentMoves);
        else
            findMovesLeft(boardState, possibleBoardStates, auxBoard, capture, cell, false, currentMoves);
    }


    private void findMovesRight(
            List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates, Boolean[] auxBoard,
            final AtomicBoolean capture, Cell cell,
            boolean isRed, String currentMove
    ) {
        int row = cell.getRow();
        int col = cell.getColumn();
        int pos = cell.getPosition();
        boolean rowEven = row % 2 == 0;

        int topRight = getTopRightPosition(pos, rowEven);
        int bottomRight = getBottomRightPosition(pos, rowEven);

        if (row < MAX_ROW - 1 && col > MIN_COL) {
            int topTopRight = getTopRightPosition(topRight, !rowEven);
            maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture, pos, topRight, topTopRight, isRed, currentMove, cell.isKing());
        }

        if (row < MAX_ROW - 1 && col < MAX_COL) {
            int bottomBottomRight = getBottomRightPosition(bottomRight, !rowEven);
            maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture, pos, bottomRight, bottomBottomRight, isRed, currentMove, cell.isKing());
        }

        if (!capture.get() && row != MAX_ROW)
            maybeMove(boardState, possibleBoardStates, auxBoard,
                    isRed, col, pos, rowEven, topRight, bottomRight);
    }


    private void findMovesLeft(
            List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates, Boolean[] auxBoard,
            AtomicBoolean capture, Cell cell,
            boolean isRed, String currentMove
    ) {
        int row = cell.getRow();
        int col = cell.getColumn();
        int pos = cell.getPosition();
        boolean rowEven = row % 2 == 0;

        int topLeft = getTopLeftPosition(pos, rowEven);
        int bottomLeft = getBottomLeftPosition(pos, rowEven);

        if (row > MIN_ROW + 1 && col > MIN_COL) {
            int topTopLeft = getTopLeftPosition(topLeft, !rowEven);
            maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture, pos, topLeft, topTopLeft, isRed, currentMove, cell.isKing());
        }

        if (row > MIN_ROW + 1 && col < MAX_COL) {
            int bottomBottomLeft = getBottomLeftPosition(bottomLeft, !rowEven);
            maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture, pos, bottomLeft, bottomBottomLeft, isRed, currentMove, cell.isKing());
        }

        if (!capture.get() && row != MIN_ROW)
            maybeMove(boardState, possibleBoardStates, auxBoard,
                    isRed, col, pos, rowEven, topLeft, bottomLeft);
    }


    private void maybeMove(List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates,
                           Boolean[] auxBoard, boolean isRed,
                           int col, int pos, boolean rowEven, int top, int bottom) {
        if ((col != MIN_COL || rowEven) && isEmpty(top, auxBoard))
            possibleBoardStates.add(move(boardState, pos, top, isRed));

        if ((col != MAX_COL || !rowEven) && isEmpty(bottom, auxBoard))
            possibleBoardStates.add(move(boardState, pos, bottom, isRed));
    }


    private void maybeCapture(List<Cell> board, List<Tuple<List<Cell>, String>> children, Boolean[] auxBoard,
                              AtomicBoolean capture, int pos, int captured, int newPos,
                              boolean isRed, String currentMove, boolean isKing) {
        boolean cond = isRed ? isBlack(captured, auxBoard) : isRed(captured, auxBoard);
        if (cond && isEmpty(newPos, auxBoard)) {
            if (!capture.get()) {
                children.clear();
                capture.set(true);
            }
            Tuple<List<Cell>, String> captureMove = capture(board, pos, captured, newPos, isRed);
            if (!currentMove.isEmpty())
                currentMove += " ";
            captureMove.setSecond(currentMove + captureMove.getSecond());
            children.add(captureMove);

            Cell newCell = getNewCell(newPos, isKing, isRed);
            findMove(captureMove.getFirst(), children, capture, newCell, isRed, captureMove.getSecond());
        }
    }


    private Tuple<List<Cell>, String> capture(List<Cell> board,
                                              int prevPos, int capturedPos, int nextPos, boolean red) {
        Tuple<List<Cell>, String> move = move(board, prevPos, nextPos, red);

        List<Cell> newBoard = move.getFirst();
        int i = 0;
        for (Cell cell : newBoard) {
            if (cell.getPosition() == capturedPos) {
                newBoard.remove(i);
                break;
            }
            ++i;
        }

        return move;
    }


    private Tuple<List<Cell>, String> move(List<Cell> board,
                                           int prevPos, int nextPos, boolean isRed) {
        List<Cell> newBoard = new LinkedList<>(board);
        int i = 0;
        for (Cell cell : board) {
            if (cell.getPosition() == prevPos) {
                Cell removed = newBoard.remove(i);
                Cell newCell = getNewCell(nextPos, removed.isKing(), isRed);
                newBoard.add(i, newCell);
                break;
            }
            ++i;
        }

        return new Tuple<>(newBoard, prevPos + "," + nextPos);
    }


    private Cell getNewCell(int newPos, boolean isKing, boolean isRed) {
        int newRow = (newPos - 1) / (MAX_COL + 1);
        int newCol = (newPos - 1) % (MAX_COL + 1);
        return new Cell(isRed ? PlayerColor.RED : PlayerColor.BLACK,
                newRow, newCol, isKing || newRow == 0 || newRow == 7, newPos);
    }


    private int getTopLeftPosition(int position, boolean rowEven) {
        return rowEven ? position - 4 : position - 5;
    }


    private int getBottomLeftPosition(int position, boolean rowEven) {
        return rowEven ? position - 3 : position - 4;
    }


    private int getTopRightPosition(int position, boolean rowEven) {
        return rowEven ? position + 4 : position + 3;
    }


    private int getBottomRightPosition(int position, boolean rowEven) {
        return rowEven ? position + 5 : position + 4;
    }


    private boolean isRed(int position, Boolean[] board) {
        return board[position] == Boolean.TRUE;
    }


    private boolean isBlack(int position, Boolean[] board) {
        return board[position] == Boolean.FALSE;
    }


    private boolean isEmpty(int position, Boolean[] board) {
        return board[position] == null;
    }


    // isRed left - black left + 0.5 * (isRed kings - black kings)
    private double evaluateBoard(List<Cell> board) {
        double score = 0;

        for (Cell cell : board) {
            if (cell.isRed())
                score += cell.isKing() ? 4 : 1;
            else
                score -= cell.isKing() ? 4 : 1;
        }

        return score;
    }


    private boolean gameOver(List<Cell> board) {
        boolean hasRed = false;
        boolean hasBlack = false;
        for (Cell cell : board) {
            if (cell.isRed())
                hasRed = true;
            else
                hasBlack = true;
        }

        return (hasRed && !hasBlack) || (!hasRed && hasBlack);
    }


    private boolean isTimeOut() {
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        return timeElapsed > time;
    }


    public void resetTime() {
        start = Instant.now();
    }
}
