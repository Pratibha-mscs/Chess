package com.chess.backend.core.piece;

import com.chess.backend.core.board.Board;
import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;

import java.util.List;

public interface IPiece  extends Cloneable{
    Color getColor();
    List<Position> getLegalMoves(Board board, Position from);
    boolean canMove(Board board, Position from, Position to);
    IPiece clone();
}
