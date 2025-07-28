package com.chess.backend.core.engine;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.model.Move;
import com.chess.backend.core.board.Position;
import com.chess.backend.core.piece.IPiece;

public class MoveValidator {
    public boolean isValidMove(Board board, Move move) {
        Position from = new Position(move.getFromRow(), move.getFromCol());
        Position to = new Position(move.getToRow(), move.getToCol());

        IPiece piece = board.getPieceAt(from);
        return piece != null && piece.canMove(board, from, to);
    }
}
