package com.chess.backend.core.engine;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.model.Move;
import com.chess.backend.core.board.Position;
import lombok.Getter;

public class GameEngine {
    @Getter
    private final Board board;
    private final MoveValidator validator;

    public GameEngine(Board board) {
        this.board = board;
        this.validator = new MoveValidator();
    }

    public boolean makeMove(Move move) {
        if (!validator.isValidMove(board, move)) return false;
        Position from = new Position(move.getFromRow(), move.getFromCol());
        Position to = new Position(move.getToRow(), move.getToCol());
        board.movePiece(from, to);
        return true;
    }

}
