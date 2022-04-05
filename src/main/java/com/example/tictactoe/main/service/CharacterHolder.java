package com.example.tictactoe.main.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Scope("prototype")
public class CharacterHolder {

    private String firstName;

    private boolean localGame = false;

    private boolean repService = false;

    private boolean onlineGame = false;

    public boolean checkInvolve(){ return localGame || repService || onlineGame; }

    public void dropCondition() {
        localGame = false;
        repService = false;
        onlineGame = false;
    }
}
