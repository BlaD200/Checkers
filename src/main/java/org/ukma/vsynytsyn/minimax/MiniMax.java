package org.ukma.vsynytsyn.minimax;

import org.ukma.vsynytsyn.dto.Cell;
import org.ukma.vsynytsyn.dto.PlayerColor;
import org.ukma.vsynytsyn.utils.Tuple;

import java.util.LinkedList;
import java.util.List;

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

        double eval = maximizingPlayer ? Double.MIN_VALUE : Double.MAX_VALUE;

        List<Tuple<List<Cell>, String>> children = children(board.getFirst(), maximizingPlayer);
        Tuple<List<Cell>, String> nextBoard = children.get(0);

        for (Tuple<List<Cell>, String> child : children) {
            Tuple<Tuple<List<Cell>, String>, Double> boardEval =
                    miniMax(child, depth - 1, !maximizingPlayer, alpha, beta);
            double newEval = boardEval.getSecond();

            if (maximizingPlayer) {
                if (newEval > eval) {
                    eval = newEval;
                    nextBoard = child;
                    alpha = newEval;
                }
            } else {
                if (newEval < eval) {
                    eval = newEval;
                    nextBoard = child;
                    beta = newEval;
                }
            }

            if (beta <= alpha)
                break;
        }

        return new Tuple<>(nextBoard, eval);
    }

    // TODO remove?
//    private boolean gameOver(List<Cell> board) {
//        boolean hasRed = false;
//        boolean hasBlack = false;
//        for (Cell cell : board) {
//            if (cell.red())
//                hasRed = true;
//            else
//                hasBlack = true;
//        }
//
//        return !hasRed && !hasBlack;
//    }

    private List<Tuple<List<Cell>, String>> children(List<Cell> board, boolean red) {
        List<Tuple<List<Cell>, String>> children = new LinkedList<>();
        boolean capture = false;

        Boolean[] auxBoard = new Boolean[32]; // True - RED, False - Black, Null - Empty
        for (Cell cell : board)
            auxBoard[cell.getPosition()] = cell.red();

        for (Cell cell : board) {
            int row = cell.getRow();
            int col = cell.getColumn();
            int pos = cell.getPosition();
            boolean rowEven = row % 2 == 0;

            if (cell.red() && red) {
                if (cell.isKing()) {
                    // TODO king's movement
                } else {
                    if (row < MAX_ROW - 1 && col > MIN_COL) {
                        int topRight = getTopRightPosition(pos, rowEven);
                        int topTopRight = getTopRightPosition(topRight, !rowEven);
                        capture = maybeCapture(board, children, auxBoard, capture, pos, topRight, topTopRight, true);
                    }

                    if (row < MAX_ROW - 1 && col < MAX_COL) {
                        int bottomRight = getBottomRightPosition(pos, rowEven);
                        int bottomBottomRight = getBottomRightPosition(bottomRight, !rowEven);
                        capture = maybeCapture(board, children, auxBoard, capture, pos, bottomRight, bottomBottomRight, true);
                    }

                    if (!capture) {
                        if (col != MIN_COL && rowEven) {
                            int topRight = getTopRightPosition(pos, rowEven);
                            if (isEmpty(topRight, auxBoard))
                                children.add(move(board, pos, topRight, true));
                        }

                        if (col != MAX_COL && !rowEven) {
                            int bottomRight = getBottomRightPosition(pos, rowEven);
                            if (isEmpty(bottomRight, auxBoard))
                                children.add(move(board, pos, bottomRight, true));
                        }
                    }
                }
            } else if (!cell.red() && !red) {
                if (row > MIN_ROW + 1 && col > MIN_COL) {
                    int topLeft = getTopLeftPosition(pos, rowEven);
                    int topTopLeft = getTopLeftPosition(topLeft, !rowEven);
                    capture = maybeCapture(board, children, auxBoard, capture, pos, topLeft, topTopLeft, false);
                }

                if (row > MIN_ROW + 1 && col < MAX_COL) {
                    int bottomLeft = getBottomLeftPosition(pos, rowEven);
                    int bottomBottomLeft = getBottomLeftPosition(bottomLeft, !rowEven);
                    capture = maybeCapture(board, children, auxBoard, capture, pos, bottomLeft, bottomBottomLeft, false);
                }

                if (!capture) {
                    if (col != MIN_COL && !rowEven) {
                        int topLeft = getTopLeftPosition(pos, rowEven);
                        if (isEmpty(topLeft, auxBoard))
                            children.add(move(board, pos, topLeft, false));
                    }

                    if (col != MAX_COL && rowEven) {
                        int bottomLeft = getBottomLeftPosition(pos, rowEven);
                        if (isEmpty(bottomLeft, auxBoard))
                            children.add(move(board, pos, bottomLeft, false));
                    }
                }
            }
        }

        return children;
    }

    private boolean maybeCapture(List<Cell> board, List<Tuple<List<Cell>, String>> children, Boolean[] auxBoard,
                                 boolean capture, int pos, int captured, int newPos, boolean red) {
        boolean cond = red ? isRed(pos, auxBoard) : isBlack(pos, auxBoard);
        if (cond && isEmpty(newPos, auxBoard)) {
            if (!capture) {
                children.clear();
                capture = true;
            }
            children.add(capture(board, pos, captured, newPos, red));
        }
        return capture;
    }

    // red left - black left + 0.5 * (red kings - black kings)
    private double evaluateBoard(List<Cell> board, boolean red) {
        double score = 0;

        for (Cell cell : board) {
            if (cell.red())
                score += cell.isKing() ? 4 : 1;
            else
                score -= cell.isKing() ? 4 : 1;
        }

        score += red ? 1 : -1;

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
        int newRow = nextPos / 4 - 1;
        int newCol = nextPos % 4 - 1;
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

        return new Tuple<>(newBoard, "[" + prevPos + "," + nextPos + "]");
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
        return board[position];
    }

    private boolean isBlack(int position, Boolean[] board) {
        return !board[position];
    }

    private boolean isEmpty(int position, Boolean[] board) {
        return board[position] == null;
    }
}
