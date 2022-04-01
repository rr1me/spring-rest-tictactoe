package com.example.tictactoe.main.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "steps")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class StepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private int num;

    @NotNull
    private int playerId;

    @NotNull
    private int actualStep;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity gameId;

    public StepEntity(int num, int playerId, int actualStep, GameEntity gameId) {
        this.num = num;
        this.playerId = playerId;
        this.actualStep = actualStep;
        this.gameId = gameId;
    }
}
