package com.chess.backend.ws;

import com.chess.backend.dto.GameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendGameUpdate(String gameId, GameResponse response) {
        messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
    }
}