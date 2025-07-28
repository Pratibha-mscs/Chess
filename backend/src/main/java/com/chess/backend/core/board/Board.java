package com.chess.backend.core.board;

import com.chess.backend.core.piece.IPiece;
import com.chess.backend.core.piece.King;
import com.chess.backend.model.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    private final IPiece[][] board;

    public Board() {
        board = new IPiece[8][8];
    }

    public IPiece getPieceAt(Position pos) {
        return board[pos.row][pos.col];
    }

    public void setPieceAt(Position pos, IPiece piece) {
        board[pos.row][pos.col] = piece;
    }

    public void movePiece(Position from, Position to) {
        IPiece piece = getPieceAt(from);
        setPieceAt(to, piece);
        setPieceAt(from, null);
    }

    public boolean isPathClear(Position from, Position to) {
        // To be implemented for rook/bishop/queen
        return true;
    }

    public boolean isCheckmate(Color color) {
        System.out.println("🔍 Checking checkmate for: " + color);

        // 1. First, check if the king is even in check
        if (!isKingInCheck(color)) {
            System.out.println("🟢 King is not in check, so not checkmate.");
            return false;
        }

        System.out.println("🚨 King IS in check. Searching for any legal escape moves...");

        // 2. For every piece of the given color, check if any move gets out of check
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Position from = new Position(r, c);
                IPiece piece = getPieceAt(from);

                if (piece != null && piece.getColor() == color) {
                    List<Position> legalMoves = getLegalMoves(from);

                    System.out.printf("🧩 %s at %s has %d legal moves: %s%n",
                            piece.getClass().getSimpleName(), from, legalMoves.size(), legalMoves);

                    // If any legal move exists, it's not checkmate
                    if (!legalMoves.isEmpty()) {
                        System.out.println("✅ Escape found. Not checkmate.");
                        return false;
                    }
                }
            }
        }

        // 3. No legal moves found
        System.out.println("💀 No escape moves. This is CHECKMATE!");
        return true;
    }


    public boolean isStalemate(Color color) {
        if (isKingInCheck(color)) return false;

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                IPiece piece = getPieceAt(new Position(r, c));
                if (piece != null && piece.getColor() == color) {
                    List<Position> legal = piece.getLegalMoves(this, new Position(r, c));
                    if (!legal.isEmpty()) return false;
                }
            }
        }
        return true;
    }

    public boolean isKingInCheck(Color kingColor) {
        Position kingPos = findKingPosition(kingColor);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position from = new Position(row, col);
                IPiece piece = getPieceAt(from);

                if (piece != null && piece.getColor() != kingColor) {
                    if (piece.canMove(this, from, kingPos)) {
                        System.out.printf(" -> King under attack by %s from %s\n", piece.getClass().getSimpleName(), from.getClass());
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Position findKingPosition(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = getPieceAt(new Position(row, col));
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(row, col);
                }
            }
        }
        return null; // should never happen
    }

    public List<Position> getLegalMoves(Position from) {
        if (!from.isInsideBoard()) return Collections.emptyList();
        IPiece piece = getPieceAt(from);
        if (piece == null) return Collections.emptyList();

        List<Position> legalMoves = new ArrayList<>();

        for (Position to : piece.getLegalMoves(this, from)) {
            // ✅ simulate the board
            Board copy = this.copy();
            copy.movePiece(from, to);

            // ✅ now check if king is in check after this move
            if (!copy.isKingInCheck(piece.getColor())) {
                legalMoves.add(to); // ✅ this move is safe
            } else {
                System.out.printf("SKIP: %s → %s puts/keeps king in check\n", from, to);
            }
        }
        return legalMoves;
    }


    public Board copy() {
        Board newBoard = new Board();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                IPiece piece = this.board[row][col];
                if (piece != null) {
                    newBoard.board[row][col] = piece.clone(); // Requires all pieces to implement clone()
                }
            }
        }
        return newBoard;
    }
}
