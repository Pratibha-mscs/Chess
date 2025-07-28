package com.chess.backend.controller;

import com.chess.backend.dto.MoveRequest;
import com.chess.backend.service.GameService;
import com.chess.backend.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameWebSocketController {

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/{gameId}/move")
    public void handleMove(@DestinationVariable String gameId, MoveRequest move) {
        Game updatedGame = gameService.processMove(gameId, move);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, updatedGame);
    }
}
