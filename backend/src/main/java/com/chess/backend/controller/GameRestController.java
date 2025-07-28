package com.chess.backend.controller;


import com.chess.backend.core.board.Position;
import com.chess.backend.dto.GameResponse;
import com.chess.backend.dto.GameStateDTO;
import com.chess.backend.dto.MoveRequest;
import com.chess.backend.model.Color;
import com.chess.backend.model.Game;
import com.chess.backend.model.Move;
import com.chess.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
public class GameRestController {

    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public Game createGame(@RequestParam String whitePlayer, @RequestParam String blackPlayer) {
        return gameService.createGame(whitePlayer, blackPlayer);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGame(@PathVariable String gameId) {
        try {
            Game game = gameService.getGameById(gameId);
            return ResponseEntity.ok(GameStateDTO.fromGame(game)); // 🔥 send DTO
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Game not found with ID: " + gameId);
        }
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<?> makeMove(@PathVariable String gameId, @RequestBody MoveRequest moveRequest) {
        try {
            GameResponse updated = gameService.makeMove(gameId, moveRequest);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            GameResponse partial = gameService.partialGameStateOnError(gameId);
            return ResponseEntity
                    .badRequest()
                    .body(partial);
        }
    }

    @GetMapping("/{gameId}/legal-moves")
    public List<Position> getLegalMoves(
            @PathVariable String gameId,
            @RequestParam int row,
            @RequestParam int col
    ) {
        return gameService.getLegalMoves(gameId, row, col);
    }

    @PostMapping("/new")
    public GameResponse createNewGame() {
        // You should implement this
        Game game = gameService.createGameWithDefaults();
        return GameResponse.fromGame(game);
    }

    @PostMapping("/{gameId}/resign")
    public GameResponse resign(@PathVariable String gameId, @RequestParam("player") Color player) {
        return gameService.resignGame(gameId, player);
    }

    @PostMapping("/{gameId}/offer-draw")
    public GameResponse offerDraw(@PathVariable String gameId, @RequestParam("player") Color player) {
        return gameService.offerDraw(gameId, player);
    }

    @GetMapping("/{gameId}/moves")
    public ResponseEntity<List<Move>> getMoveHistory(@PathVariable String gameId) {
        try {
            List<Move> moves = gameService.getMoveHistory(gameId);
            return ResponseEntity.ok(moves);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}