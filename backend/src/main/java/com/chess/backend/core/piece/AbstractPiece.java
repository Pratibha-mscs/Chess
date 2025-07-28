package com.chess.backend.core.piece;

import com.chess.backend.model.Color;

public abstract class AbstractPiece implements IPiece {
    protected final Color color;

    public AbstractPiece(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public abstract IPiece clone();
}
