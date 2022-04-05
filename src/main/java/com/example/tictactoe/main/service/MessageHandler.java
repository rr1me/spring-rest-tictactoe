package com.example.tictactoe.main.service;

import com.example.tictactoe.main.onlineGame.OnlineGameRegisterer;
import com.example.tictactoe.main.service.botServices.FileHandler;
import com.example.tictactoe.main.service.botServices.LocalGame;
import com.example.tictactoe.main.service.botServices.RepService;
import com.example.tictactoe.main.util.SendMsg;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageHandler {

    private final OnlineGameRegisterer onlineGameRegisterer;
    private final ObjectProvider<CharacterHolder> characterHolderObjectProvider;

    private final LocalGame localGame;
    private final RepService repService;
    private final FileHandler fileHandler;

    private final SendMsg sendMsg;

    private Map<Long, CharacterHolder> characterHolderMap = new HashMap<>();;

    public MessageHandler(OnlineGameRegisterer onlineGameRegisterer, ObjectProvider<CharacterHolder> characterHolderObjectProvider, LocalGame localGame, RepService repService, FileHandler fileHandler, SendMsg sendMsg) {
        this.onlineGameRegisterer = onlineGameRegisterer;
        this.characterHolderObjectProvider = characterHolderObjectProvider;
        this.localGame = localGame;
        this.repService = repService;
        this.fileHandler = fileHandler;
        this.sendMsg = sendMsg;
    }

    public void d(Update update){
        Message message = update.getMessage();

        long chatId = message.getChatId();

        CharacterHolder characterHolder;
        if (!characterHolderMap.containsKey(chatId)){

            characterHolder = characterHolderObjectProvider.getObject();
            characterHolder.setFirstName(message.getFrom().getFirstName());

            characterHolderMap.put(chatId, characterHolder);
        }else{
            characterHolder = characterHolderMap.get(chatId);
        }

        handle(update, characterHolder);

    }

    private void handle(Update update, CharacterHolder characterHolder){
        Message message = update.getMessage();


        if (message.hasText()){

            if (message.getText().startsWith("/") ) {
                commandExec(update, characterHolder);
            }

            else {

            }


        }


    }


    private void commandExec(Update update, CharacterHolder characterHolder){
        String cmd = update.getMessage().getText();

        if (characterHolder.checkInvolve()){

            if (cmd.contains("/exit")){
                if (characterHolder.isOnlineGame())
                    onlineGameRegisterer.exit(characterHolder.getFirstName());
                characterHolder.dropCondition();

                sendMsg.exec(update, "Exit confirmed");
            }else{
                sendMsg.exec(update, "You're already involved in smth, try /exit");
            }

        }
        else {
            if (cmd.contains("/start"))
                start(update);
            else if (cmd.contains("/game")) {

                characterHolder.setLocalGame(localGame.reg(update));
            }
        }
    }

    private void start(Update update){
        sendMsg.exec(update, "Type /game to play\nType /rep to reproduce the game");
    }
}
