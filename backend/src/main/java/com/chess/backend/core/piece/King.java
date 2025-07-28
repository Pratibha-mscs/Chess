package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.List;

public class King extends AbstractPiece {
    public King(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        if (!to.isInsideBoard()) return false;

        int dr = Math.abs(to.row - from.row);
        int dc = Math.abs(to.col - from.col);
        if (dr > 1 || dc > 1) return false;

        IPiece target = board.getPieceAt(to);
        return target == null || target.getColor() != this.color;
    }

    @Override
    public IPiece clone() {
        return new King(this.color);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] deltas = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] d : deltas) {
            Position p = new Position(from.row + d[0], from.col + d[1]);
            if (!p.isInsideBoard()) continue;
            IPiece piece = board.getPieceAt(p);
            if (piece == null || piece.getColor() != this.color) {
                moves.add(p);
            }
        }

        return moves;
    }

}
