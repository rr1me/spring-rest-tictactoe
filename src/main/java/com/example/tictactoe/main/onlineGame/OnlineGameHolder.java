package com.example.tictactoe.main.onlineGame;

import com.example.tictactoe.main.service.ActualGame;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
//@AllArgsConstructor
@Scope("prototype")
@Getter
@Setter
public class OnlineGameHolder {

    private ActualGame game;

    private long firstPlayerChatId;
    private String firstPlayerName;

    private long secondPlayerChatId;
    private String secondPlayerName;

    public long registerGame(){
        game.register(firstPlayerName, secondPlayerName);
        return firstPlayerChatId;
    }
}
