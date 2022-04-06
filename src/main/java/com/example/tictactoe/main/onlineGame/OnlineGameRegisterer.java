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

    private Map<Integer, OnlineGameHolder> gameHolderMap = new HashMap<>();

    public boolean connect(Update update, CharacterHolder characterHolder){
        List<String> args = argScan.s(update);

        int gameId;
        OnlineGameHolder onlineGameHolder;
        try{
            gameId = Integer.parseInt(args.get(1));

            onlineGameHolder = gameHolderMap.get(gameId);
            if (onlineGameHolder == null)
                throw new OutOfBoundsExcp();

        }catch (IndexOutOfBoundsException e){
            return IOOBExcp(update);
        }catch (OutOfBoundsExcp e){
            sendMsg.exec(update, "There's no game with that id");
            return false;
        }catch (NumberFormatException e){
            if (args.get(1).equals("random")){
                try {
                    onlineGameHolder = gameHolderMap.values().stream().filter(x -> x.getSecondPlayerCharacterHolder() == null).findFirst().get();

                    gameId = onlineGameHolder.getFirstPlayerCharacterHolder().getOnlineGameId();
                }catch (NoSuchElementException ignored){
                    sendMsg.exec(update, "There's no games to connect. Create your own game by using /onlinegame");
                    return false;
                };
            }else{
                return IOOBExcp(update);
            }
        }

        onlineGameHolder.setSecondPlayerCharacterHolder(characterHolder);

        characterHolder.setOnlineGameHolder(onlineGameHolder);

        long firstPlayerChatId = onlineGameHolder.registerGame();

        gameHolderMap.put(gameId, onlineGameHolder);

        StringBuilder builder = new StringBuilder("Everyone connected. Now, let the game begin\n\n");

        String firstPlayerName = onlineGameHolder.getFirstPlayerCharacterHolder().getFirstName();
        builder.append(firstPlayerName+" vs "+characterHolder.getFirstName()+"\n\n");

        builder.append(onlineGameHolder.getGame().writeBoard());

        builder.append("\nTurn: "+firstPlayerName);

        sendMsg.exec(update, builder);

        builder.append("\n\nMake your steps");

        sendMsg.exec(firstPlayerChatId, builder);

        characterHolder.setOnlineGame(true);
        characterHolder.setOnlineGameId(gameId);

        return true;
    }

    private boolean IOOBExcp(Update update){
        sendMsg.exec(update, "If you want to connect to random game type /connect random");
        return false;
    }

    public boolean reg(Update update, CharacterHolder characterHolder) {
        OnlineGameHolder onlineGameHolder = onlineGameHolderObjectProvider.getObject();
        onlineGameHolder.setGame(actualGameObjectProvider.getObject());
        onlineGameHolder.setFirstPlayerCharacterHolder(characterHolder);

        try{
            int maxKey = Collections.max(gameHolderMap.keySet());
            System.out.println(maxKey+ " "+ gameHolderMap.keySet());
            for(int i = 0; i <= maxKey+1; i++){
                if (!gameHolderMap.containsKey(i)){
                    gameHolderMap.put(i, onlineGameHolder);
                    sendMsg.exec(update, "Game has been registered with id: "+ i);

                    characterHolder.setOnlineGameHolder(onlineGameHolder);
                    characterHolder.setOnlineGameId(i);
                }
            }
        }catch (NoSuchElementException e){
            gameHolderMap.put(0, onlineGameHolder);
            sendMsg.exec(update, "Game has been registered with id: 0");

            characterHolder.setOnlineGameHolder(onlineGameHolder);
            characterHolder.setOnlineGameId(0);
        }
        return true;
    }

    public void exit(CharacterHolder characterHolder) {

        OnlineGameHolder onlineGameHolder = characterHolder.getOnlineGameHolder();

        if (characterHolder.getFirstName().equals(onlineGameHolder.getFirstPlayerCharacterHolder().getFirstName())) {

            CharacterHolder secondPlayerCharacterHolder = onlineGameHolder.getSecondPlayerCharacterHolder();
            if (secondPlayerCharacterHolder != null) {
                secondPlayerCharacterHolder.setOnlineGame(false);
                sendMsg.exec(secondPlayerCharacterHolder.getChatId(), "Your opponent leaved the game");
            }
        } else {
            CharacterHolder firstPlayerCharacterHolder = onlineGameHolder.getFirstPlayerCharacterHolder();

            sendMsg.exec(firstPlayerCharacterHolder.getChatId(), "Your opponent leaved the game");

            firstPlayerCharacterHolder.setOnlineGame(false);
        }

        gameHolderMap.remove(characterHolder.getOnlineGameId());
    }
}
