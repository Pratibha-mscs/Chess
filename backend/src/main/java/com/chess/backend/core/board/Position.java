package com.chess.backend.core.board;

public class Position {
    public final int row;
    public final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isInsideBoard() {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

}
