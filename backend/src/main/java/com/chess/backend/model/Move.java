package com.chess.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonBackReference
    private Game game;

    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    @Enumerated(EnumType.STRING)
    private PieceType pieceType;

    private boolean isCapture;

    private String promotion; // e.g., "QUEEN" if a pawn was promoted

    private int moveNumber;

    private Instant timestamp;
}
