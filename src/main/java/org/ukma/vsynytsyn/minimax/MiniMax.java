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


    public Tuple<Tuple<List<Cell>, String>, Double> miniMax(Tuple<List<Cell>, String> board, int depth,
                                                            boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 /*|| gameOver(board.getFirst())*/) {
            return new Tuple<>(board, evaluateBoard(board.getFirst(), maximizingPlayer));
        }

        double eval = maximizingPlayer ? -100 : 100;

        List<Tuple<List<Cell>, String>> possibleBoardStates = getPossibleBoardStates(board.getFirst(), maximizingPlayer);
        Tuple<List<Cell>, String> nextBoard = possibleBoardStates.get(0);

        for (Tuple<List<Cell>, String> state : possibleBoardStates) {
            Tuple<Tuple<List<Cell>, String>, Double> boardEval =
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


    private List<Tuple<List<Cell>, String>> getPossibleBoardStates(List<Cell> boardState, boolean red) {
        List<Tuple<List<Cell>, String>> possibleBoardStates = new LinkedList<>();
        AtomicBoolean capture = new AtomicBoolean(false);

        Boolean[] auxBoard = new Boolean[33]; // True - RED, False - Black, Null - Empty
        for (Cell cell : boardState)
            auxBoard[cell.getPosition()] = cell.isRed();

        for (Cell cell : boardState) {
            if (cell.isRed() && red) {
                if (cell.isKing())
                    findMovesLeft(boardState, possibleBoardStates, auxBoard, capture, cell, true);
                findMovesRight(boardState, possibleBoardStates, auxBoard, capture, cell, true);
            } else if (!cell.isRed() && !red) {
                if (cell.isKing())
                    findMovesRight(boardState, possibleBoardStates, auxBoard, capture, cell, false);
                findMovesLeft(boardState, possibleBoardStates, auxBoard, capture, cell, false);
            }
        }

        return possibleBoardStates;
    }


    private void findMovesRight(
            List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates, Boolean[] auxBoard,
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
            capture.set(maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture.get(), pos, topRight, topTopRight, isRed));
        }

        if (row < MAX_ROW - 1 && col < MAX_COL) {
            int bottomBottomRight = getBottomRightPosition(bottomRight, !rowEven);
            capture.set(maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture.get(), pos, bottomRight, bottomBottomRight, isRed));
        }

        maybeMove(boardState, possibleBoardStates,
                auxBoard, capture, isRed, row, col, pos, rowEven, topRight, bottomRight);
    }


    private void findMovesLeft(
            List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates, Boolean[] auxBoard,
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
            capture.set(maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture.get(), pos, topLeft, topTopLeft, isRed));
        }

        if (row > MIN_ROW + 1 && col < MAX_COL) {
            int bottomBottomLeft = getBottomLeftPosition(bottomLeft, !rowEven);
            capture.set(maybeCapture(boardState, possibleBoardStates, auxBoard,
                    capture.get(), pos, bottomLeft, bottomBottomLeft, isRed));
        }

        maybeMove(boardState, possibleBoardStates,
                auxBoard, capture, isRed, row, col, pos, rowEven, topLeft, bottomLeft);
    }


    private void maybeMove(List<Cell> boardState, List<Tuple<List<Cell>, String>> possibleBoardStates,
                           Boolean[] auxBoard, AtomicBoolean capture, boolean isRed,
                           int row, int col, int pos, boolean rowEven, int top, int bottom) {
        if (!capture.get() && row != (isRed ? MAX_ROW : MIN_ROW)) {
            if ((col != MIN_COL || rowEven) && isEmpty(top, auxBoard))
                possibleBoardStates.add(move(boardState, pos, top, isRed));

            if ((col != MAX_COL || !rowEven) && isEmpty(bottom, auxBoard))
                possibleBoardStates.add(move(boardState, pos, bottom, isRed));
        }
    }


    private boolean maybeCapture(List<Cell> board, List<Tuple<List<Cell>, String>> children, Boolean[] auxBoard,
                                 boolean capture, int pos, int captured, int newPos, boolean red) {
        boolean cond = red ? isBlack(captured, auxBoard) : isRed(captured, auxBoard);
        if (cond && isEmpty(newPos, auxBoard)) {
            if (!capture) {
                children.clear();
                capture = true;
            }
            children.add(capture(board, pos, captured, newPos, red));
        }
        return capture;
    }


    // isRed left - black left + 0.5 * (isRed kings - black kings)
    private double evaluateBoard(List<Cell> board, boolean red) {
        double score = 0;

        for (Cell cell : board) {
            if (cell.isRed())
                score += cell.isKing() ? 4 : 1;
            else
                score -= cell.isKing() ? 4 : 1;
        }

        return score;
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


    // TODO optimize (HashMap<Position, Cell>?)
    private Tuple<List<Cell>, String> move(List<Cell> board,
                                           int prevPos, int nextPos, boolean red) {
        int newRow = (nextPos - 1) / (MAX_COL + 1);
        int newCol = (nextPos - 1) % (MAX_COL + 1);
        Cell newCell = new Cell(red ? PlayerColor.RED : PlayerColor.BLACK,
                newRow, newCol, newRow == 0 || newRow == 7, nextPos);

        List<Cell> newBoard = new LinkedList<>(board);
        int i = 0;
        for (Cell cell : board) {
            if (cell.getPosition() == prevPos) {
                newBoard.remove(i);
                newBoard.add(i, newCell);
                break;
            }
            ++i;
        }

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


    private boolean isRed(int position, Boolean[] board) {
        return board[position] == Boolean.TRUE;
    }


    private boolean isBlack(int position, Boolean[] board) {
        return board[position] == Boolean.FALSE;
    }


    private boolean isEmpty(int position, Boolean[] board) {
        return board[position] == null;
    }
}
