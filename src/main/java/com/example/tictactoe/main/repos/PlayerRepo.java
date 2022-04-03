package com.example.tictactoe.main.repos;

import com.example.tictactoe.main.entities.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepo extends JpaRepository<PlayerEntity, Long> {
    PlayerEntity findByName(String name);
}
