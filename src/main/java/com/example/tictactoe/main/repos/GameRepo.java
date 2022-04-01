package com.example.tictactoe.main.repos;

import com.example.tictactoe.main.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends JpaRepository<GameEntity, Long> {
    GameEntity findById(long id);
}
