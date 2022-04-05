package com.example.tictactoe.main.service;

import com.example.tictactoe.main.onlineGame.OnlineGameRegisterer;
import com.example.tictactoe.main.service.botServices.FileHandler;
import com.example.tictactoe.main.service.botServices.LocalGame;
import com.example.tictactoe.main.service.botServices.RepService;
import com.example.tictactoe.main.util.ArgScan;
import com.example.tictactoe.main.util.SendMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Getter
@Setter
public class BotExecutor {

    private final OnlineGameRegisterer onlineGameRegisterer;
    private final FileHandler fileHandler;
    private final LocalGame localGame;
    private final RepService repService;

    private final SendMsg sendMsg;
    private final ArgScan argScan;

    @Autowired
    public BotExecutor(OnlineGameRegisterer onlineGameRegisterer, FileHandler fileHandler, LocalGame localGame, RepService repService, SendMsg sendMsg, ArgScan argScan) {
        this.fileHandler = fileHandler;
        this.onlineGameRegisterer = onlineGameRegisterer;
        this.localGame = localGame;
        this.repService = repService;
        this.sendMsg = sendMsg;
        this.argScan = argScan;
    }

    public void run(Update update) {



        Message message = update.getMessage();

        if (message.hasText())
            if (message.getText().startsWith("/")) {
                commandEx(update);
            } else {
                try{
//                    if (onlineGameRegisterer.getGameHandlerMap().values().stream().anyMatch(x->x.getFirstPlayerChatId() == update.getMessage().getChatId()) ){
//
//                        Map.Entry<Integer, OnlineGameHolder> onlineGameHolderEntry = onlineGameRegisterer.getGameHandlerMap().entrySet().stream()
//                                .filter(x->x.getValue().getFirstPlayerChatId() == update.getMessage().getChatId()).findFirst().get();
//
////                        onlineStep(update, onlineGameHolderEntry);
//
//                    }
//                    else if (onlineGameRegisterer.getGameHandlerMap().values().stream().anyMatch(x->x.getSecondPlayerChatId() == update.getMessage().getChatId()) ){
//
//                        Map.Entry<Integer, OnlineGameHolder> onlineGameHolderEntry = onlineGameRegisterer.getGameHandlerMap().entrySet().stream()
//                                .filter(x->x.getValue().getSecondPlayerChatId() == update.getMessage().getChatId()).findFirst().get();
//
////                        onlineStep(update, onlineGameHolderEntry);
//                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        else if(!message.hasText()){
            String caption = message.getCaption();
            if (caption != null){
                if (caption.contains("/rep"))
                    fileHandler.upload(update);
                else
                    noCmd(update);
            }
        }
    }

    private void commandEx(Update update) {
        String cmd = update.getMessage().getText();
        try{
//            if (cmd.contains("/game"))
//                localGame.reg(update,);
            if (cmd.contains("/rep"))
                repService.rep(update);
            else if (cmd.contains("/start"))
                start(update);
//            else if (cmd.contains("/onlinegame"))
//                onlineGameRegisterer.reg(update);
//            else if (cmd.contains("/connect"))
//                onlineGameRegisterer.connect(update);
            else
                noCmd(update);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void onlineStep(Update update, Map.Entry<Integer, OnlineGameHolder> onlineGameHolderEntry) {
//        OnlineGameHolder onlineGameHolder = onlineGameHolderEntry.getValue();
//
//        ActualGame game = onlineGameHolder.getGame();
//
//        if (!update.getMessage().getFrom().getFirstName().equals(game.getTempPlayerName())){
//
//            sendMsg.exec(update, "Not your turn");
//        }
//        else{
//            StringBuilder builder = game.makeStep(update.getMessage().getText());
//            if (builder.toString().contains("won as") || builder.toString().contains("Draw"))
//                onlineGameRegisterer.getGameHandlerMap().remove(onlineGameHolderEntry.getKey());
//
//            sendMsg.exec(onlineGameHolder.getFirstPlayerChatId(), builder);
//            sendMsg.exec(onlineGameHolder.getSecondPlayerChatId(), builder);
//        }
//    }

    private void noCmd(Update update){
        sendMsg.exec(update, "There is no such command. Try /start");
    }

    private void start(Update update){
        sendMsg.exec(update, "Type /game to play\nType /rep to reproduce the game");
    }
}
