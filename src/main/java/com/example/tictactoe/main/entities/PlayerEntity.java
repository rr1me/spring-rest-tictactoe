package com.example.tictactoe.main.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class PlayerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @NotNull
    private int played;

    @NotNull
    private int wins;

    @NotNull
    private int draws;

    public PlayerEntity(String name, int played, int wins, int draws) {
        this.name = name;
        this.played = played;
        this.wins = wins;
        this.draws = draws;
    }

    public void changeStats(String result){
        played += 1;

        switch (result) {
            case "win" -> wins += 1;
            case "draw" -> draws += 1;
        }

    }
}
