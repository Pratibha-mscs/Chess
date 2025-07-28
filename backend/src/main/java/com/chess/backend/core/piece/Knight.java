package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.List;

public class Knight extends AbstractPiece {
    public Knight(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int dr = Math.abs(from.row - to.row);
        int dc = Math.abs(from.col - to.col);
        if (!((dr == 2 && dc == 1) || (dr == 1 && dc == 2))) return false;

        IPiece target = board.getPieceAt(to);
        return target == null || target.getColor() != this.color;
    }

    @Override
    public IPiece clone() {
        return new Knight(this.color);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] deltas = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
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
