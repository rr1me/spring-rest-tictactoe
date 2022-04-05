package com.example.tictactoe.main.service.botServices;

import com.example.tictactoe.main.service.ActualGame;
import com.example.tictactoe.main.util.ArgScan;
import com.example.tictactoe.main.util.SendMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.List;

@Service
public class LocalGame {

    private final ActualGame game;

    private final SendMsg sendMsg;
    private final ArgScan argScan;

    @Getter
    @Setter
    private boolean registered = false;

    @Autowired
    public LocalGame(ActualGame game, SendMsg sendMsg, ArgScan argScan) {
        this.game = game;
        this.sendMsg = sendMsg;
        this.argScan = argScan;
    }

    public boolean reg(Update update){
        StringBuilder builder = new StringBuilder();

        List<String> args = argScan.s(update);

        if (args.size() > 2 && !registered){
            String firstPlayer = args.get(1);
            String secondPlayer = args.get(2);
            builder.append("""
                    Registered
                    Now make your steps
                    """);
            game.register(firstPlayer, secondPlayer);
            sendMsg.exec(update, builder.append(game.writeBoard()) );
            registered = !registered;

            return true;
        }else if(registered){
            sendMsg.exec(update, builder.append("""
                    Already registered
                    Make your step
                    """));

            return true;
        }
        else{
            sendMsg.exec(update, builder.append("""
                    To register the local game use
                    /game firstPlayerName(X) secondPlayerName(O)
                    
                    To register the internet game use /onlinegame
                    
                    To connect to an existing game use /connect
                    """));

            return false;
        }

//        sendMsg.exec(update, builder);
    }

    public void step(Update update) throws IOException {
        StringBuilder builder;
        if (registered) {
            builder = game.makeStep(update.getMessage().getText());

            if (builder.toString().contains("won as") || builder.toString().contains("Draw"))
                registered = false;

            sendMsg.exec(update, builder);
        }
        else
            sendMsg.exec(update, "Use /game to register the game\nUse /rep to reproduce game");
    }
}
