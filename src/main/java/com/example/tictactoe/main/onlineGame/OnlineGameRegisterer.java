package com.example.tictactoe.main.onlineGame;

import com.example.tictactoe.main.exceptions.OutOfBoundsExcp;
import com.example.tictactoe.main.service.ActualGame;
import com.example.tictactoe.main.service.CharacterHolder;
import com.example.tictactoe.main.util.ArgScan;
import com.example.tictactoe.main.util.SendMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Service
@Getter
@Setter
public class OnlineGameRegisterer {

    private ObjectProvider<OnlineGameHolder> onlineGameHolderObjectProvider;
    private ObjectProvider<ActualGame> actualGameObjectProvider;

    private ArgScan argScan;
    private SendMsg sendMsg;

    @Autowired
    public OnlineGameRegisterer(ObjectProvider<OnlineGameHolder> onlineGameHolderObjectProvider, ObjectProvider<ActualGame> actualGameObjectProvider, ArgScan argScan, SendMsg sendMsg) {
        this.onlineGameHolderObjectProvider = onlineGameHolderObjectProvider;
        this.actualGameObjectProvider = actualGameObjectProvider;
        this.argScan = argScan;
        this.sendMsg = sendMsg;
    }

    private Map<Integer, OnlineGameHolder> gameHandlerMap = new HashMap<>();

    public boolean connect(Update update, CharacterHolder characterHolder){
        if (gameHandlerMap.values().stream()
                .anyMatch(x->x.getFirstPlayerCharacterHolder().getChatId() == update.getMessage().getChatId()) ) {
            sendMsg.exec(update, "Not for you");
            return false;
        }
        else{
            List<String> args = argScan.s(update);

            try{
                int gameId = Integer.parseInt(args.get(1));

                OnlineGameHolder onlineGameHolder = gameHandlerMap.get(gameId);
                if (onlineGameHolder == null)
                    throw new OutOfBoundsExcp();

                Long secondPlayerChatId = update.getMessage().getChatId();
                onlineGameHolder.setSecondPlayerCharacterHolder(characterHolder);

                characterHolder.setOnlineGameHolder(onlineGameHolder);

                long firstPlayerChatId = onlineGameHolder.registerGame();

                gameHandlerMap.put(gameId, onlineGameHolder);

                StringBuilder builder = new StringBuilder("Everyone connected. Now, let the game begin\n\n");
                StringBuilder board = onlineGameHolder.getGame().writeBoard();

                builder.append(board);
                builder.append("\nMake your steps");

                sendMsg.exec(firstPlayerChatId, builder);
                sendMsg.exec(update, builder);

                characterHolder.setOnlineGame(true);
                characterHolder.setOnlineGameId(gameId);

                return true;

            }catch (IndexOutOfBoundsException e){
                sendMsg.exec(update, "If you want to connect to random game type /connect random");
                return false;
                // TODO: 04.04.2022 make random connection
            }catch (OutOfBoundsExcp e){
                sendMsg.exec(update, "There's no game with that id");
                return false;
            }
        }
    }

    public boolean reg(Update update, CharacterHolder characterHolder) {
        OnlineGameHolder onlineGameHolder = onlineGameHolderObjectProvider.getObject();
        onlineGameHolder.setGame(actualGameObjectProvider.getObject());
        onlineGameHolder.setFirstPlayerCharacterHolder(characterHolder);

        try{
            int maxKey = Collections.max(gameHandlerMap.keySet());
            System.out.println(maxKey+ " "+gameHandlerMap.keySet());
            for(int i = 0; i <= maxKey+1; i++){
                if (!gameHandlerMap.containsKey(i)){
                    gameHandlerMap.put(i, onlineGameHolder);
                    sendMsg.exec(update, "Game has been registered with id: "+ i);

                    characterHolder.setOnlineGameHolder(onlineGameHolder);
                    characterHolder.setOnlineGameId(i);
                }
            }
        }catch (NoSuchElementException e){
            gameHandlerMap.put(0, onlineGameHolder);
            sendMsg.exec(update, "Game has been registered with id: 0");

            characterHolder.setOnlineGameHolder(onlineGameHolder);
            characterHolder.setOnlineGameId(0);
        }
        return true;
    }

    public void exit(CharacterHolder characterHolder){
//        Set<Map.Entry<Integer, OnlineGameHolder>> mapSet = gameHandlerMap.entrySet();
//
//        Map.Entry<Integer, OnlineGameHolder> onlineGameHolderEntry;

//        if (mapSet.stream().anyMatch(x->x.getValue().getFirstPlayerCharacterHolder().getFirstName().equals(firstName))){
//            onlineGameHolderEntry = mapSet.stream().filter(x->x.getValue().getFirstPlayerCharacterHolder().getFirstName().equals(firstName)).findFirst().get();
//
//            try {
//                long chatId = onlineGameHolderEntry.getValue().getSecondPlayerCharacterHolder().getChatId();
//                sendMsg.exec(onlineGameHolderEntry.getValue().getSecondPlayerCharacterHolder().getChatId(), "Your opponent leaved the game");
//            }catch (NullPointerException e){
//            }
//
//        }else{
//            onlineGameHolderEntry = mapSet.stream().filter(x->x.getValue().getSecondPlayerCharacterHolder().getFirstName().equals(firstName)).findFirst().get();
//
//            sendMsg.exec(onlineGameHolderEntry.getValue().getFirstPlayerCharacterHolder().getChatId(), "Your opponent leaved the game");
//        }

        OnlineGameHolder onlineGameHolder = characterHolder.getOnlineGameHolder();

        if (characterHolder.getFirstName().equals(onlineGameHolder.getFirstPlayerCharacterHolder().getFirstName())){

            CharacterHolder secondPlayerCharacterHolder = onlineGameHolder.getSecondPlayerCharacterHolder();
            if (secondPlayerCharacterHolder != null){
                secondPlayerCharacterHolder.setOnlineGame(false);
                sendMsg.exec(secondPlayerCharacterHolder.getChatId(), "Your opponent leaved the game");
            }
        }else{
            CharacterHolder firstPlayerCharacterHolder = onlineGameHolder.getFirstPlayerCharacterHolder();

            sendMsg.exec(firstPlayerCharacterHolder.getChatId(), "Your opponent leaved the game");

            firstPlayerCharacterHolder.setOnlineGame(false);
        }

        gameHandlerMap.remove(characterHolder.getOnlineGameId());

//        if (onlineGameHolderEntry.getValue().getFirstPlayerName() == firstName){
//
//        }
    }
}
