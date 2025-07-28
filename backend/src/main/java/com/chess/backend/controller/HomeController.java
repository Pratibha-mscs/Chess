package com.chess.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "♟️ Welcome to the Chess Backend API! Try /api/game/create to start a game.";
    }
}
