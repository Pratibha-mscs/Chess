package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.List;

public class Rook extends AbstractPiece {
    public Rook(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        if (from.row != to.row && from.col != to.col) return false;

        int stepRow = Integer.compare(to.row, from.row);
        int stepCol = Integer.compare(to.col, from.col);

        int r = from.row + stepRow;
        int c = from.col + stepCol;
        while (r != to.row || c != to.col) {
            if (board.getPieceAt(new Position(r, c)) != null) return false;
            r += stepRow;
            c += stepCol;
        }

        IPiece target = board.getPieceAt(to);
        return target == null || target.getColor() != this.color;
    }

    @Override
    public IPiece clone() {
        return new Rook(this.color);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}
        };

        for (int[] d : directions) {
            for (int i = 1; i < 8; i++) {
                Position p = new Position(from.row + i * d[0], from.col + i * d[1]);
                if (!p.isInsideBoard()) break;
                IPiece piece = board.getPieceAt(p);
                if (piece == null) {
                    moves.add(p);
                } else {
                    if (piece.getColor() != this.color) moves.add(p);
                    break;
                }
            }
        }

        return moves;
    }
}
