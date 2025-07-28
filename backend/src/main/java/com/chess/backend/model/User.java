package com.chess.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class User {

    @Id
    private String id;

    private String username;

    @OneToMany(mappedBy = "whitePlayer")
    private List<Game> whiteGames = new ArrayList<>();

    @OneToMany(mappedBy = "blackPlayer")
    private List<Game> blackGames = new ArrayList<>();
}
