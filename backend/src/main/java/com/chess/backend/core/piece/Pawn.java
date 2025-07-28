package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends AbstractPiece {
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public boolean canMove(Board board, Position from, Position to) {
        int direction = (this.color == Color.WHITE) ? -1 : 1;
        int startRow  = (this.color == Color.WHITE) ? 6 : 1;
        int dr = to.row - from.row;
        int dc = to.col - from.col;

        // Forward move
        if (dc == 0) {
            if (dr == direction && board.getPieceAt(to) == null) return true;
            if (dr == 2 * direction && from.row == startRow &&
                    board.getPieceAt(to) == null &&
                    board.getPieceAt(new Position(from.row + direction, from.col)) == null) return true;
        }

        // Capture
        if (Math.abs(dc) == 1 && dr == direction) {
            IPiece target = board.getPieceAt(to);
            return target != null && target.getColor() != this.color;
        }

        return false;
    }

    @Override
    public IPiece clone() {
        return new Pawn(this.color);
    }

    @Override
    public List<Position> getLegalMoves(Board board, Position from) {
        List<Position> moves = new ArrayList<>();
        int dir = (color == Color.WHITE) ? -1 : 1;

        // Forward move
        Position oneStep = new Position(from.row + dir, from.col);
        if (oneStep.isInsideBoard() && board.getPieceAt(oneStep) == null) {
            moves.add(oneStep);

            // First move double step
            boolean isStartRow = (color == Color.WHITE && from.row == 6) || (color == Color.BLACK && from.row == 1);
            Position twoSteps = new Position(from.row + 2 * dir, from.col);
            if (isStartRow && board.getPieceAt(twoSteps) == null) {
                moves.add(twoSteps);
            }
        }

        // Diagonal captures
        for (int dCol : new int[]{-1, 1}) {
            Position diag = new Position(from.row + dir, from.col + dCol);
            if (diag.isInsideBoard()) {
                IPiece piece = board.getPieceAt(diag);
                if (piece != null && piece.getColor() != this.color) {
                    moves.add(diag);
                }
            }
        }

        return moves;
    }

}
