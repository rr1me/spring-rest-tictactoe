package com.example.tictactoe.main.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String firstPlayer;

    @NotNull
    private String secondPlayer;

    @OneToMany(mappedBy = "gameId", cascade = CascadeType.ALL)
    private List<StepEntity> steps;

    @NotNull
    private int winner;

    public GameEntity(String firstPlayer, String secondPlayer, int winner) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.winner = winner;
    }

    private String winner(){
        return switch (winner){
            case 1 -> firstPlayer;
            case 2 -> secondPlayer;
            case 3 -> "Draw";
            default -> throw new IllegalStateException("Unexpected value: " + winner);
        };
    }

    @Override
    public String toString() {
        return "GameEntity[" +
                "id=" + id +
                ", firstPlayer='" + firstPlayer + '\'' +
                ", secondPlayer='" + secondPlayer + '\'' +
                ", winner='" + winner() + '\'' +
                ']';
    }
}
