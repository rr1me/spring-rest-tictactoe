package com.example.tictactoe.main.onlineGame;

import com.example.tictactoe.main.service.ActualGame;
import com.example.tictactoe.main.service.CharacterHolder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Getter
@Setter
public class OnlineGameHolder {

    private ActualGame game;

    private CharacterHolder firstPlayerCharacterHolder;
    private CharacterHolder secondPlayerCharacterHolder;

    public long registerGame(){
        game.register(firstPlayerCharacterHolder.getFirstName(), secondPlayerCharacterHolder.getFirstName(), firstPlayerCharacterHolder);
        return firstPlayerCharacterHolder.getChatId();
    }
}
