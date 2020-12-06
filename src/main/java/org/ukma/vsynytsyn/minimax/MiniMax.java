package org.ukma.vsynytsyn.minimax;

import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.utils.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class MiniMax {

    public static final int MIN_ROW = 0;
    public static final int MAX_ROW = 7;
    public static final int MIN_COL = 0;
    public static final int MAX_COL = 3;


    public Tuple<Tuple<Cell[], String>, Double> miniMax(Tuple<Cell[], String> board, int depth,
                                                            boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0)
            return new Tuple<>(board, evaluateBoard(board.getFirst()));

        double eval = maximizingPlayer ? -100 : 100;

        List<Tuple<Cell[], String>> possibleBoardStates = getPossibleBoardStates(board.getFirst(), maximizingPlayer);
        if (possibleBoardStates.size() == 0)
            if (gameOver(board.getFirst()))
                return new Tuple<>(board, (maximizingPlayer ? 100. : -100.));
            else // checkers are blocked
                return new Tuple<>(board, (maximizingPlayer ? 0. : 0.));

        Tuple<Cell[], String> nextBoard = possibleBoardStates.get(0);

        for (Tuple<Cell[], String> state : possibleBoardStates) {
            Tuple<Tuple<Cell[], String>, Double> boardEval =
                    miniMax(state, depth - 1, !maximizingPlayer, alpha, beta);
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


    private boolean gameOver(Cell[] board) {
        boolean hasRed = false;
        boolean hasBlack = false;
        for (Cell cell : board) {
            if (cell == null)
                continue;

            if (cell.isRed())
                hasRed = true;
            else
                hasBlack = true;
        }

        return (hasRed && !hasBlack) || (!hasRed && hasBlack);
    }


    private List<Tuple<Cell[], String>> getPossibleBoardStates(Cell[] boardState, boolean red) {
        List<Tuple<Cell[], String>> possibleBoardStates = new LinkedList<>();
        AtomicBoolean capture = new AtomicBoolean(false);

        for (Cell cell : boardState) {
            if (cell == null)
                continue;

            if (cell.isRed() && red) {
                if (cell.isKing())
                    findMovesLeft(boardState, possibleBoardStates, capture, cell, true);
                findMovesRight(boardState, possibleBoardStates, capture, cell, true);
            } else if (!cell.isRed() && !red) {
                if (cell.isKing())
                    findMovesRight(boardState, possibleBoardStates, capture, cell, false);
                findMovesLeft(boardState, possibleBoardStates, capture, cell, false);
            }
        }

        return possibleBoardStates;
    }


    private void findMovesRight(
            Cell[] boardState, List<Tuple<Cell[], String>> possibleBoardStates,
            final AtomicBoolean capture, Cell cell,
            boolean isRed
    ) {
        int row = cell.getRow();
        int col = cell.getColumn();
        int pos = cell.getPosition();
        boolean rowEven = row % 2 == 0;

        int topRight = getTopRightPosition(pos, rowEven);
        int bottomRight = getBottomRightPosition(pos, rowEven);

        if (row < MAX_ROW - 1 && col > MIN_COL) {
            int topTopRight = getTopRightPosition(topRight, !rowEven);
            capture.set(maybeCapture(boardState, possibleBoardStates,
                    capture.get(), pos, topRight, topTopRight, isRed));
        }

        if (row < MAX_ROW - 1 && col < MAX_COL) {
            int bottomBottomRight = getBottomRightPosition(bottomRight, !rowEven);
            capture.set(maybeCapture(boardState, possibleBoardStates,
                    capture.get(), pos, bottomRight, bottomBottomRight, isRed));
        }

        if (!capture.get() && row != MAX_ROW)
            maybeMove(boardState, possibleBoardStates,
                    isRed, col, pos, rowEven, topRight, bottomRight);
    }


    private void findMovesLeft(
            Cell[] boardState, List<Tuple<Cell[], String>> possibleBoardStates,
            AtomicBoolean capture, Cell cell,
            boolean isRed
    ) {
        int row = cell.getRow();
        int col = cell.getColumn();
        int pos = cell.getPosition();
        boolean rowEven = row % 2 == 0;

        int topLeft = getTopLeftPosition(pos, rowEven);
        int bottomLeft = getBottomLeftPosition(pos, rowEven);

        if (row > MIN_ROW + 1 && col > MIN_COL) {
            int topTopLeft = getTopLeftPosition(topLeft, !rowEven);
            capture.set(maybeCapture(boardState, possibleBoardStates,
                    capture.get(), pos, topLeft, topTopLeft, isRed));
        }

        if (row > MIN_ROW + 1 && col < MAX_COL) {
            int bottomBottomLeft = getBottomLeftPosition(bottomLeft, !rowEven);
            capture.set(maybeCapture(boardState, possibleBoardStates,
                    capture.get(), pos, bottomLeft, bottomBottomLeft, isRed));
        }

        if (!capture.get() && row != MIN_ROW)
            maybeMove(boardState, possibleBoardStates,
                    isRed, col, pos, rowEven, topLeft, bottomLeft);
    }


    private void maybeMove(Cell[] boardState, List<Tuple<Cell[], String>> possibleBoardStates,
                           boolean isRed, int col, int pos, boolean rowEven, int top, int bottom) {
        if ((col != MIN_COL || rowEven) && isEmpty(top, boardState))
            possibleBoardStates.add(move(boardState, pos, top, isRed));

        if ((col != MAX_COL || !rowEven) && isEmpty(bottom, boardState))
            possibleBoardStates.add(move(boardState, pos, bottom, isRed));
    }


    private boolean maybeCapture(Cell[] boardState, List<Tuple<Cell[], String>> children,
                                 boolean capture, int pos, int captured, int newPos, boolean red) {
        boolean cond = red ? isBlack(captured, boardState) : isRed(captured, boardState);
        if (cond && isEmpty(newPos, boardState)) {
            if (!capture) {
                children.clear();
                capture = true;
            }
            children.add(capture(boardState, pos, captured, newPos, red));
        }
        return capture;
    }


    // isRed left - black left + 0.5 * (isRed kings - black kings)
    private double evaluateBoard(Cell[] board) {
        double score = 0;

        for (Cell cell : board) {
            if (cell == null)
                continue;

            if (cell.isRed())
                score += cell.isKing() ? 4 : 1;
            else
                score -= cell.isKing() ? 4 : 1;
        }

        return score;
    }


    private Tuple<Cell[], String> capture(Cell[] board, int prevPos, int capturedPos, int nextPos, boolean red) {
        Tuple<Cell[], String> move = move(board, prevPos, nextPos, red);

        Cell[] newBoard = move.getFirst();
        newBoard[capturedPos] = null;

        return move;
    }


    private Tuple<Cell[], String> move(Cell[] board, int prevPos, int nextPos, boolean red) {
        int newRow = (nextPos - 1) / (MAX_COL + 1);
        int newCol = (nextPos - 1) % (MAX_COL + 1);
        Cell newCell = new Cell(red ? PlayerColor.RED : PlayerColor.BLACK,
                newRow, newCol, newRow == 0 || newRow == 7, nextPos);

        Cell[] newBoard = new Cell[board.length];
        System.arraycopy(board, 0, newBoard, 0, board.length);

        newBoard[prevPos] = newCell;

        return new Tuple<>(newBoard, prevPos + "," + nextPos);
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


    private boolean isRed(int position, Cell[] board) {
        Cell cell = board[position];
        return cell != null && cell.isRed();
    }


    private boolean isBlack(int position, Cell[] board) {
        Cell cell = board[position];
        return cell != null && !cell.isRed();
    }


    private boolean isEmpty(int position, Cell[] board) {
        return board[position] == null;
    }


    public static Cell[] preprocessBoard(List<Cell> cells) {
        Cell[] newBoard = new Cell[33];

        for (Cell cell : cells)
            newBoard[cell.getPosition()] = cell;

        return newBoard;
    }
}
