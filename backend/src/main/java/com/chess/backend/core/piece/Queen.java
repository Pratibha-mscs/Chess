package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.List;

public class Queen extends AbstractPiece {
    public Queen(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        return new Rook(this.color).canMove(board, from, to) ||
                new Bishop(this.color).canMove(board, from, to);
    }

    @Override
    public IPiece clone() {
        return new Queen(this.color);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Rook-like
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Bishop-like
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
