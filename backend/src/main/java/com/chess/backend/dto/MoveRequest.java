package com.chess.backend.dto;

import com.chess.backend.model.PieceType;
import lombok.Data;

@Data
public class MoveRequest {
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private PieceType pieceType;       // Required for move validation
    private String promotionPiece;     // Optional for pawn promotion
}
