package com.chess.backend.config;

import com.chess.backend.model.Color;
import com.chess.backend.model.Game;
import com.chess.backend.model.GameStatus;
import com.chess.backend.model.User;
import com.chess.backend.repository.GameRepository;
import com.chess.backend.repository.UserRepository;
import com.chess.backend.repository.MoveRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;

    @Bean
    CommandLineRunner seedInitialData() {
        return args -> {
            System.out.println("🔁 Cleaning DB...");

            moveRepository.deleteAll();
            gameRepository.deleteAll();
            userRepository.deleteAll();

            System.out.println("✅ DB Cleaned. Seeding...");

            User white = new User();
            white.setId("11111111-1111-1111-1111-111111111111");
            white.setUsername("whiteplayer1");
            userRepository.save(white);

            User black = new User();
            black.setId("22222222-2222-2222-2222-222222222222");
            black.setUsername("blackplayer1");
            userRepository.save(black);

            Game game = new Game();
            game.setId("33333333-3333-3333-3333-333333333333");
            game.setWhitePlayer(white);
            game.setBlackPlayer(black);
            game.setBoardState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            game.setTurn(Color.WHITE);
            game.setStatus(GameStatus.ONGOING);
            gameRepository.save(game);

            System.out.println("✅ Default users and game seeded.");
        };
    }
}

