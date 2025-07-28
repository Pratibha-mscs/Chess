// service/GameService.java
package com.chess.backend.service;

import com.chess.backend.core.board.Position;
import com.chess.backend.dto.GameResponse;
import com.chess.backend.model.Color;
import com.chess.backend.model.Game;
import com.chess.backend.dto.MoveRequest;
import com.chess.backend.model.Move;

import java.util.List;

public interface GameService {
    Game createGame(String whitePlayer, String blackPlayer);
    Game getGameById(String gameId);
    Game processMove(String gameId, MoveRequest move);
    GameResponse makeMove(String gameId, MoveRequest moveRequest);
    List<Position> getLegalMoves(String gameId, int row, int col);
    Game createGameWithDefaults();
    GameResponse partialGameStateOnError(String gameId);
    GameResponse resignGame(String gameId, Color player);
    GameResponse offerDraw(String gameId, Color player);
    List<Move> getMoveHistory(String gameId);
}
