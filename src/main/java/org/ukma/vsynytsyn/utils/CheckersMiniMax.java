package org.ukma.vsynytsyn.utils;

import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.minimax.MiniMax;

import java.util.LinkedList;
import java.util.List;

public class CheckersMiniMax extends MiniMax<Tuple<List<Cell>, String>> {

    @Override
    protected boolean gameOver(Tuple<List<Cell>, String> position) {
        boolean hasRed = false;
        boolean hasBlack = false;
        for (Cell cell : position.getA()) {
            if (cell.getColor().equals("RED"))
                hasRed = true;
            else
                hasBlack = true;
        }

        return !hasRed && !hasBlack;
    }

    @Override
    protected List<Tuple<List<Cell>, String>> children(Tuple<List<Cell>, String> position, boolean red) {
        List<Tuple<List<Cell>, String>> children = new LinkedList<>();
        boolean capture = false;

        Boolean[] board = new Boolean[32];
        for (Cell cell : position.getA()) {
            int pos = cell.getPosition();
            boolean isRed = cell.getColor().equals("RED");
            board[pos] = isRed;
        }

        for (Cell cell : position.getA()) {
            if (red) {
                if (cell.getColor().equals("RED")) {
                    if (cell.isKing()) {

                    } else {
                        int row = cell.getRow();
                        int col = cell.getColumn();
                        int pos = cell.getPosition();
                        boolean rowEven = row % 2 == 0;

                        if (row > 1 && col > 1) {
                            int topLeft = getTopLeft(pos, rowEven);
                            int topLeftTopLeft = getTopLeft(topLeft, !rowEven);
                            if (isBlack(topLeft, board) && isEmpty(topLeftTopLeft, board)) {
                                capture = true;
                                // move
                            }
                        }

                        if (row > 1 && col < 6) {
                            int bottomLeft = getBottomLeft(pos, rowEven);
                            int bottomBottomLeft = getBottomLeft(bottomLeft, !rowEven);
                            if (isBlack(bottomLeft, board) && isEmpty(bottomBottomLeft, board)) {
                                capture = true;
                                // move
                            }
                        }

                        if (row < 6 && col > 1) {
                            int topRight = getTopRight(pos, rowEven);
                            int topTopRight = getTopRight(topRight, !rowEven);
                            if (isBlack(topRight, board) && isEmpty(topTopRight, board)) {
                                capture = true;
                                // move
                            }
                        }

                        if (row < 6 && col < 6) {
                            int bottomRight = getBottomRight(pos, rowEven);
                            int bottomBottomRight = getBottomRight(bottomRight, !rowEven);
                            if (isBlack(bottomRight, board) && isEmpty(bottomBottomRight, board)) {
                                capture = true;
                                // move
                            }
                        }

                        if (!capture) {
                            if (col != 0) {
                                int topRight = getTopRight(pos, rowEven);
                                if (isEmpty(topRight, board)) {
                                    // move
                                }
                            }

                            if (col != 7) {
                                int bottomRight = getBottomRight(pos, rowEven);
                                if (isEmpty(bottomRight, board)) {
                                    // move
                                }
                            }
                        }
                    }
                }
            } else {
                if (cell.getColor().equals("BLACK")) {

                }
            }
        }

        return children;
    }

    @Override
    protected double evaluatePosition(Tuple<List<Cell>, String> position, boolean red) {
        double score = 0;

        for (Cell cell : position.getA()) {
            if (cell.getColor().equals("RED"))
                score += cell.isKing() ? 4 : 1;
            else
                score -= cell.isKing() ? 4 : 1;
        }

        score += red ? 1 : -1;

        return score;
    }

    private int getTopLeft(int position, boolean rowEven) {
        return rowEven ? position - 4 : position - 5;
    }

    private int getBottomLeft(int position, boolean rowEven) {
        return rowEven ? position - 3 : position - 4;
    }

    private int getTopRight(int position, boolean rowEven) {
        return rowEven ? position + 4 : position + 3;
    }

    private int getBottomRight(int position, boolean rowEven) {
        return rowEven ? position + 5 : position + 4;
    }

    private boolean isRed(int position, Boolean[] board) {
        return board[position];
    }

    private boolean isBlack(int position, Boolean[] board) {
        return !board[position];
    }

    private boolean isEmpty(int position, Boolean[] board) {
        return board[position] == null;
    }
}
