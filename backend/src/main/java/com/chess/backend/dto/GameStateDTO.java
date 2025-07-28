package com.chess.backend.dto;

import com.chess.backend.model.Game;
import com.chess.backend.model.Move;
import lombok.Data;

import java.util.List;
@Data
public class GameStateDTO {
    private String gameId;
    private String whitePlayerUsername;
    private String blackPlayerUsername;
    private String boardState;
    private String turn;
    private List<Move> moves;
    private String status;

    public static GameStateDTO fromGame(Game game) {
        GameStateDTO dto = new GameStateDTO();
        dto.setGameId(game.getId());
        dto.setWhitePlayerUsername(game.getWhitePlayer().getUsername());
        dto.setBlackPlayerUsername(game.getBlackPlayer().getUsername());
        dto.setBoardState(game.getBoardState());
        dto.setTurn(game.getTurn().name());
        dto.setStatus(game.getStatus().name());
        dto.setMoves(game.getMoves());
        return dto;
    }
}
