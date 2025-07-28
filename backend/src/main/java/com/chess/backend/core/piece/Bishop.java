package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends AbstractPiece  {
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int rowDiff = Math.abs(to.row - from.row);
        int colDiff = Math.abs(to.col - from.col);

        if (rowDiff != colDiff) return false; // must be diagonal

        int rowDir = Integer.compare(to.row - from.row, 0);
        int colDir = Integer.compare(to.col - from.col, 0);

        int r = from.row + rowDir;
        int c = from.col + colDir;

        while (r != to.row || c != to.col) {
            if (board.getPieceAt(new Position(r, c)) != null) return false; // path blocked
            r += rowDir;
            c += colDir;
        }

        IPiece targetPiece = board.getPieceAt(to);
        return targetPiece == null || targetPiece.getColor() != this.color;
    }


    @Override
    public IPiece clone() {
        return new Bishop(this.color);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Position from) {
        List<Position> legalMoves = new ArrayList<>();

        // Bishop moves diagonally: 4 directions
        int[][] directions = {
                {1, 1}, {-1, -1}, {-1, 1}, {1, -1}
        };

        for (int[] dir : directions) {
            int dRow = dir[0], dCol = dir[1];
            for (int i = 1; i < 8; i++) {
                int newRow = from.row + i * dRow;
                int newCol = from.col + i * dCol;
                Position next = new Position(newRow, newCol);

                if (!next.isInsideBoard()) break;
                var occupyingPiece = board.getPieceAt(next);
                if (occupyingPiece == null) {
                    legalMoves.add(next);
                } else {
                    if (occupyingPiece.getColor() != this.color) {
                        legalMoves.add(next); // capture allowed
                    }
                    break; // cannot jump over any piece
                }
            }
        }

        return legalMoves;
    }
}
