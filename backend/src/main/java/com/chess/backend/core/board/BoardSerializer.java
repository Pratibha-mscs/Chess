package com.chess.backend.core.board;

import com.chess.backend.core.piece.*;
import com.chess.backend.model.Color;

public class BoardSerializer {

    public static String toFEN(Board board) {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                IPiece piece = board.getPieceAt(new Position(row, col));
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }
                    fen.append(getFENChar(piece));
                }
            }
            if (empty > 0) fen.append(empty);
            if (row != 7) fen.append('/');
        }
        return fen.toString(); // Only piece placement (no castling/turn for now)
    }

    public static Board fromFEN(String fen) {
        Board board = new Board();

        // Only use the piece-placement part of the FEN
        String[] rows = fen.split(" ")[0].split("/");

        for (int r = 0; r < 8; r++) {
            String row = rows[r];
            int col = 0;
            for (char ch : row.toCharArray()) {
                if (Character.isDigit(ch)) {
                    col += ch - '0';
                } else {
                    IPiece piece = createPieceFromFEN(ch);
                    board.setPieceAt(new Position(r, col), piece);
                    col++;
                }
            }
        }
        return board;
    }

    private static char getFENChar(IPiece piece) {
        char ch;
        switch (piece.getClass().getSimpleName()) {
            case "Pawn":   ch = 'p'; break;
            case "Knight": ch = 'n'; break;
            case "Bishop": ch = 'b'; break;
            case "Rook":   ch = 'r'; break;
            case "Queen":  ch = 'q'; break;
            case "King":   ch = 'k'; break;
            default:       ch = '?';
        }
        return piece.getColor() == Color.WHITE ? Character.toUpperCase(ch) : ch;
    }

    private static IPiece createPieceFromFEN(char ch) {
        Color color = Character.isUpperCase(ch) ? Color.WHITE : Color.BLACK;
        return switch (Character.toLowerCase(ch)) {
            case 'p' -> new Pawn(color);
            case 'r' -> new Rook(color);
            case 'n' -> new Knight(color);
            case 'b' -> new Bishop(color);
            case 'q' -> new Queen(color);
            case 'k' -> new King(color);
            default -> throw new IllegalArgumentException("Invalid FEN character: " + ch);
        };
    }
}
