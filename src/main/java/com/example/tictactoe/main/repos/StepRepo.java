package com.example.tictactoe.main.repos;

import com.example.tictactoe.main.entities.StepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepo extends JpaRepository<StepEntity, Long> {
    StepEntity findById(long id);
}
