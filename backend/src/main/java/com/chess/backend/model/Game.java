package com.chess.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    @Id
    private String id;

    @ManyToOne
    private User whitePlayer;

    @ManyToOne
    private User blackPlayer;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private Color turn;

    @Column(columnDefinition = "TEXT")
    private String boardState; // e.g., FEN string or custom format

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Move> moves = new ArrayList<>();

    public void addMove(Move move) {
        this.moves.add(move);
        move.setGame(this);
    }

    public void switchTurn() {
        this.turn = (this.turn == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    public void initializeBoard() {
        this.boardState = "start"; // Replace with actual FEN setup if needed
        this.turn = Color.WHITE;
        this.status = GameStatus.ONGOING;
    }
}
