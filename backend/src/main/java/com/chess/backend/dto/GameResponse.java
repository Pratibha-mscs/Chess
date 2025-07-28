package com.chess.backend.dto;

import com.chess.backend.core.board.Position;
import com.chess.backend.model.Color;
import com.chess.backend.model.Game;
import com.chess.backend.model.GameStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class GameResponse {

    private String id;
    private String boardState;
    private GameStatus status;
    private Color turn;

    // Optional: usernames
    private String whitePlayer;
    private String blackPlayer;
    private Position checkPosition;

    // Static factory method to convert from Game entity
    public static GameResponse fromGame(Game game) {
        GameResponse dto = new GameResponse();
        dto.setId(game.getId());
        dto.setBoardState(game.getBoardState());
        dto.setStatus(game.getStatus());

        if (game.getTurn() != null)
            dto.setTurn(game.getTurn());

        if (game.getWhitePlayer() != null)
            dto.setWhitePlayer(game.getWhitePlayer().getUsername());

        if (game.getBlackPlayer() != null)
            dto.setBlackPlayer(game.getBlackPlayer().getUsername());

        return dto;
    }
}
