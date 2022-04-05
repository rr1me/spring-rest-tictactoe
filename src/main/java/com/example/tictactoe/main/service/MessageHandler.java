package com.example.tictactoe.main.service;

import com.example.tictactoe.main.service.botServices.FileHandler;
import com.example.tictactoe.main.service.botServices.LocalGame;
import com.example.tictactoe.main.service.botServices.OnlineGame;
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

    private final OnlineGame onlineGame;
    private final ObjectProvider<CharacterHolder> characterHolderObjectProvider;

    private final LocalGame localGame;
    private final RepService repService;
    private final FileHandler fileHandler;

    private final SendMsg sendMsg;

    private final Map<Long, CharacterHolder> characterHolderMap = new HashMap<>();

    public MessageHandler(OnlineGame onlineGame, ObjectProvider<CharacterHolder> characterHolderObjectProvider, LocalGame localGame, RepService repService, FileHandler fileHandler, SendMsg sendMsg) {
        this.onlineGame = onlineGame;
        this.characterHolderObjectProvider = characterHolderObjectProvider;
        this.localGame = localGame;
        this.repService = repService;
        this.fileHandler = fileHandler;
        this.sendMsg = sendMsg;
    }

    public void run(Update update){
        Message message = update.getMessage();

        long chatId = message.getChatId();

        CharacterHolder characterHolder;
        if (!characterHolderMap.containsKey(chatId)){

            characterHolder = characterHolderObjectProvider.getObject();
            characterHolder.setFirstName(message.getFrom().getFirstName());
            characterHolder.setChatId(chatId);

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
                if (characterHolder.isLocalGame())
                    localGame.step(update, characterHolder);
                else if (characterHolder.isRepService())
                    repService.choose(update, characterHolder);
                else if (characterHolder.isOnlineGame())
                    onlineGame.onlineStep(update, characterHolder);
            }
        }else{
            if (characterHolder.checkInvolve()){
                involved(update);
            }else{
                if (message.getCaption() != null && message.getCaption().contains("/rep")){
                    fileHandler.upload(update);
                }else
                    noCmd(update);
            }
        }
    }


    private void commandExec(Update update, CharacterHolder characterHolder){
        String cmd = update.getMessage().getText();

        if (characterHolder.checkInvolve()){

            if (cmd.contains("/exit")){
                if (characterHolder.isOnlineGame())
                    onlineGame.exit(characterHolder);
                characterHolder.dropCondition();

                sendMsg.exec(update, "Exit confirmed");
            }else{
                involved(update);
            }
        }
        else {
            if (cmd.contains("/start"))
                start(update);
            else if (cmd.contains("/game"))
                characterHolder.setLocalGame(localGame.reg(update, characterHolder));
            else if (cmd.contains("/rep"))
                characterHolder.setRepService(repService.rep(update));
            else if (cmd.contains("/onlinegame"))
                characterHolder.setOnlineGame(onlineGame.reg(update, characterHolder));
            else if (cmd.contains("/connect"))
                characterHolder.setOnlineGame(onlineGame.connect(update, characterHolder));
            else if (cmd.contains("/exit"))
                sendMsg.exec(update, "?");
            else if (cmd.contains("/format"))
                fileHandler.changeFormat(update, characterHolder);
            else
                noCmd(update);
        }
    }

    private void noCmd(Update update){
        sendMsg.exec(update, "There is no such command. Try /start");
    }

    private void involved(Update update){
        sendMsg.exec(update, "You're already involved in smth, try /exit");
    }

    private void start(Update update){
        sendMsg.exec(update, "Type /game to play\nType /rep to reproduce the game");
    }
}
