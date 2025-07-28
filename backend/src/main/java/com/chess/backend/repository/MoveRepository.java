package com.chess.backend.repository;

import com.chess.backend.model.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import com.chess.backend.model.Game;

@Repository
public interface MoveRepository extends JpaRepository<Move, UUID> {
    List<Move> findByGame(Game game);
}
