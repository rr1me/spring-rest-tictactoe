package com.example.tictactoe.main.onlineGame;

import com.example.tictactoe.main.exceptions.OutOfBoundsExcp;
import com.example.tictactoe.main.service.ActualGame;
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

    public void connect(Update update){
        if (gameHandlerMap.values().stream().anyMatch(x->x.getFirstPlayerChatId() == update.getMessage().getChatId()) )
            sendMsg.exec(update, "Not for you");
        else{
            List<String> args = argScan.s(update);

            try{
                int gameId = Integer.parseInt(args.get(1));

                OnlineGameHolder onlineGameHolder = gameHandlerMap.get(gameId);
                if (onlineGameHolder == null)
                    throw new OutOfBoundsExcp();

                Long secondPlayerChatId = update.getMessage().getChatId();
                onlineGameHolder.setSecondPlayerChatId(secondPlayerChatId);

                onlineGameHolder.setSecondPlayerName(update.getMessage().getFrom().getFirstName());

                long firstPlayerChatId = onlineGameHolder.registerGame();

                gameHandlerMap.put(gameId, onlineGameHolder);

                StringBuilder builder = new StringBuilder("Everyone connected. Now, let the game begin\n\n");
                StringBuilder board = onlineGameHolder.getGame().writeBoard();

                builder.append(board);
                builder.append("\nMake your steps");

                sendMsg.exec(firstPlayerChatId, builder);
                sendMsg.exec(update, builder);

            }catch (IndexOutOfBoundsException e){
                sendMsg.exec(update, "If you want to connect to random game type /connect random");
                // TODO: 04.04.2022 make random connection
            }catch (OutOfBoundsExcp e){
                sendMsg.exec(update, "There's no game with that id");
            }
        }
    }

    public void onlineGame(Update update) {
        if (gameHandlerMap.values().stream().anyMatch(x->x.getFirstPlayerChatId() == update.getMessage().getChatId()) )
            sendMsg.exec(update, "Already registered\nWait for the other player");
        else{
            OnlineGameHolder onlineGameHolder = onlineGameHolderObjectProvider.getObject();
            onlineGameHolder.setGame(actualGameObjectProvider.getObject());
            onlineGameHolder.setFirstPlayerChatId(update.getMessage().getChatId());
            onlineGameHolder.setFirstPlayerName(update.getMessage().getFrom().getFirstName());

            try{
                int maxKey = Collections.max(gameHandlerMap.keySet());
                System.out.println(maxKey+ " "+gameHandlerMap.keySet());
                for(int i = 0; i <= maxKey+1; i++){
                    if (!gameHandlerMap.containsKey(i)){
                        gameHandlerMap.put(i, onlineGameHolder);
                        sendMsg.exec(update, "Game has been registered with id: "+ i);
                    }
                }
            }catch (NoSuchElementException e){
                gameHandlerMap.put(0, onlineGameHolder);
                sendMsg.exec(update, "Game has been registered with id: 0");
            }
        }
    }

    public void exit(String firstName){
        Set<Map.Entry<Integer, OnlineGameHolder>> mapSet = gameHandlerMap.entrySet();

        Map.Entry<Integer, OnlineGameHolder> onlineGameHolderEntry;

        if (mapSet.stream().anyMatch(x->x.getValue().getFirstPlayerName().equals(firstName))){
            onlineGameHolderEntry = mapSet.stream().filter(x->x.getValue().getFirstPlayerName().equals(firstName)).findFirst().get();

            sendMsg.exec(onlineGameHolderEntry.getValue().getSecondPlayerChatId(), "Your opponent leaved the game");
        }else{
            onlineGameHolderEntry = mapSet.stream().filter(x->x.getValue().getSecondPlayerName().equals(firstName)).findFirst().get();

            sendMsg.exec(onlineGameHolderEntry.getValue().getFirstPlayerChatId(), "Your opponent leaved the game");
        }

        gameHandlerMap.remove(onlineGameHolderEntry.getKey());

//        if (onlineGameHolderEntry.getValue().getFirstPlayerName() == firstName){
//
//        }
    }
}
