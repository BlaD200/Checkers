package org.ukma.vsynytsyn.minimax;

import org.ukma.vsynytsyn.utils.Tuple;

import java.util.List;

public abstract class MiniMax<Position> {

    // alpha = Double.MIN_VALUE, beta = Double.MAX_VALUE
    public Tuple<Position, Double> miniMax(Position position, int depth,
                                           boolean maximizingPlayer, double alpha, double beta) {
        if (depth == 0 || gameOver(position)) {
            return new Tuple<>(position, evaluatePosition(position, maximizingPlayer));
        }

        double eval = maximizingPlayer ? Double.MIN_VALUE : Double.MAX_VALUE;

        List<Position> children = children(position, maximizingPlayer);
        Position nextPosition = children.get(0);

        for (Position child : children) {
            Tuple<Position, Double> positionEval = miniMax(child, depth - 1, !maximizingPlayer, alpha, beta);
            double newEval = positionEval.getB();

            if (maximizingPlayer) {
                if (newEval > eval) {
                    eval = newEval;
                    nextPosition = child;
                    alpha = newEval;
                }
            } else {
                if (newEval < eval) {
                    eval = newEval;
                    nextPosition = child;
                    beta = newEval;
                }
            }

            if (beta <= alpha)
                break;
        }

        return new Tuple<>(nextPosition, eval);
    }

    protected abstract boolean gameOver(Position position);

    protected abstract List<Position> children(Position position, boolean maximizingPlayer);

    protected abstract double evaluatePosition(Position position, boolean maximizingPlayer);
}
