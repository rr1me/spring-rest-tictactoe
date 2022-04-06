package com.example.tictactoe.main.service.botServices;

import com.example.tictactoe.main.onlineGame.OnlineGameHolder;
import com.example.tictactoe.main.onlineGame.OnlineGameRegisterer;
import com.example.tictactoe.main.service.ActualGame;
import com.example.tictactoe.main.service.CharacterHolder;
import com.example.tictactoe.main.util.SendMsg;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class OnlineGame {

    private final OnlineGameRegisterer onlineGameRegisterer;
    private final SendMsg sendMsg;

    public OnlineGame(OnlineGameRegisterer onlineGameRegisterer, SendMsg sendMsg) {
        this.onlineGameRegisterer = onlineGameRegisterer;
        this.sendMsg = sendMsg;
    }

    public void onlineStep(Update update, CharacterHolder characterHolder) {
        OnlineGameHolder onlineGameHolder = characterHolder.getOnlineGameHolder();

        ActualGame game = onlineGameHolder.getGame();

        if (!update.getMessage().getFrom().getFirstName().equals(game.getTempPlayerName())){
            sendMsg.exec(update, "Not your turn");
        }
        else{
            StringBuilder builder = game.makeStep(update.getMessage().getText(), characterHolder);

            CharacterHolder firstPlayerCharacterHolder = onlineGameHolder.getFirstPlayerCharacterHolder();
            CharacterHolder secondPlayerCharacterHolder = onlineGameHolder.getSecondPlayerCharacterHolder();
            if (builder.toString().contains("won as") || builder.toString().contains("Draw")) {
                onlineGameRegisterer.getGameHolderMap().remove(characterHolder.getOnlineGameId());

                firstPlayerCharacterHolder.setOnlineGame(false);
                secondPlayerCharacterHolder.setOnlineGame(false);
            }else{
                builder.append("\nTurn: "+game.getTempPlayerName());
            }

            if (builder.toString().contains("There's already")){
                sendMsg.exec(characterHolder.getChatId(), builder);
            }else{

                sendMsg.exec(firstPlayerCharacterHolder.getChatId(), builder);
                sendMsg.exec(secondPlayerCharacterHolder.getChatId(), builder);
            }
        }
    }

    public boolean connect(Update update, CharacterHolder characterHolder){
        return onlineGameRegisterer.connect(update, characterHolder);
    }

    public boolean reg(Update update, CharacterHolder characterHolder){
        return onlineGameRegisterer.reg(update, characterHolder);
    }

    public void exit(CharacterHolder characterHolder){
        onlineGameRegisterer.exit(characterHolder);
    }
}
