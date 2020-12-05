package org.ukma.vsynytsyn.minimax;

import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.minimax.MiniMax;
import org.ukma.vsynytsyn.utils.Tuple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CheckersMiniMax extends MiniMax<Tuple<List<Cell>, String>> {

    @Override
    protected boolean gameOver(Tuple<List<Cell>, String> position) {
        boolean hasRed = false;
        boolean hasBlack = false;
        for (Cell cell : position.getA()) {
            if (cell.red())
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
        for (Cell cell : position.getA())
            board[cell.getPosition()] = cell.red();

        for (Cell cell : position.getA()) {
            int row = cell.getRow();
            int col = cell.getColumn();
            int pos = cell.getPosition();
            boolean rowEven = row % 2 == 0;

            if (cell.red() && red) {
                if (cell.isKing()) {
                    // TODO king's movement
                } else {
                    if (row < 6 && col > 1) {
                        int topRight = getTopRight(pos, rowEven);
                        int topTopRight = getTopRight(topRight, !rowEven);
                        if (isBlack(topRight, board) && isEmpty(topTopRight, board)) {
                            if (!capture) {
                                children.clear();
                                capture = true;
                            }
                            capture(position, pos, topRight, topTopRight, true);
                        }
                    }

                    if (row < 6 && col < 6) {
                        int bottomRight = getBottomRight(pos, rowEven);
                        int bottomBottomRight = getBottomRight(bottomRight, !rowEven);
                        if (isBlack(bottomRight, board) && isEmpty(bottomBottomRight, board)) {
                            if (!capture) {
                                children.clear();
                                capture = true;
                            }
                            capture(position, pos, bottomRight, bottomBottomRight, true);
                        }
                    }

                    if (!capture) {
                        if (col != 0) {
                            int topRight = getTopRight(pos, rowEven);
                            if (isEmpty(topRight, board))
                                children.add(move(position, pos, topRight, true));
                        }

                        if (col != 7) {
                            int bottomRight = getBottomRight(pos, rowEven);
                            if (isEmpty(bottomRight, board))
                                children.add(move(position, pos, bottomRight, true));
                        }
                    }
                }
            } else if (!cell.red() && !red) {
                if (row > 1 && col > 1) {
                    int topLeft = getTopLeft(pos, rowEven);
                    int topTopLeft = getTopLeft(topLeft, !rowEven);
                    if (isRed(topLeft, board) && isEmpty(topTopLeft, board)) {
                        if (!capture) {
                            children.clear();
                            capture = true;
                        }
                        capture(position, pos, topLeft, topTopLeft, false);
                    }
                }

                if (row > 1 && col < 6) {
                    int bottomLeft = getBottomLeft(pos, rowEven);
                    int bottomBottomLeft = getBottomLeft(bottomLeft, !rowEven);
                    if (isRed(bottomLeft, board) && isEmpty(bottomBottomLeft, board)) {
                        if (!capture) {
                            children.clear();
                            capture = true;
                        }
                        capture(position, pos, bottomLeft, bottomBottomLeft, false);
                    }
                }

                if (!capture) {
                    if (col != 0) {
                        int topLeft = getTopLeft(pos, rowEven);
                        if (isEmpty(topLeft, board))
                            children.add(move(position, pos, topLeft, false));
                    }

                    if (col != 7) {
                        int bottomLeft = getBottomLeft(pos, rowEven);
                        if (isEmpty(bottomLeft, board))
                            children.add(move(position, pos, bottomLeft, false));
                    }
                }
            }
        }

        return children;
    }

    @Override
    protected double evaluatePosition(Tuple<List<Cell>, String> position, boolean red) {
        double score = 0;

        for (Cell cell : position.getA()) {
            if (cell.red())
                score += cell.isKing() ? 4 : 1;
            else
                score -= cell.isKing() ? 4 : 1;
        }

        score += red ? 1 : -1;

        return score;
    }

    private Tuple<List<Cell>, String> capture(Tuple<List<Cell>, String> position,
                                           int prev, int captured, int next, boolean red) {
        Tuple<List<Cell>, String> move = move(position, prev, next, red);

        List<Cell> newPosition = move.getA();
        int i = 0;
        for (Cell cell : newPosition) {
            if (cell.getPosition() == captured) {
                newPosition.remove(i);
                break;
            }
            ++i;
        }

        return move;
    }

    private Tuple<List<Cell>, String> move(Tuple<List<Cell>, String> position,
                                           int prev, int next, boolean red) {
        int newRow = next / 4 - 1;
        int newCol = next % 4 - 1;
        Cell newCell = new Cell(red ? PlayerColor.RED : PlayerColor.BLACK,
                newRow, newCol, newRow == 0 || newRow == 7, next);

        List<Cell> newPosition = new LinkedList<>(position.getA());
        int i = 0;
        for (Cell cell : position.getA()) {
            if (cell.getPosition() == prev) {
                newPosition.remove(i);
                newPosition.add(i, newCell);
                break;
            }
            ++i;
        }

        return new Tuple<>(newPosition, "[" + prev + "," + next + "]");
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
