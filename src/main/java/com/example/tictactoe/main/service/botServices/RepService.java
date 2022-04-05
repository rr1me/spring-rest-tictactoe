package com.example.tictactoe.main.service.botServices;

import com.example.tictactoe.main.service.CharacterHolder;
import com.example.tictactoe.main.service.Reproduction;
import com.example.tictactoe.main.util.ArgScan;
import com.example.tictactoe.main.util.SendMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
@Setter
@Getter
public class RepService {

    private final Reproduction re;
    private final LocalGame localGame;

    private final SendMsg sendMsg;
    private final ArgScan argScan;

    @Autowired
    public RepService(Reproduction re, LocalGame localGame, SendMsg sendMsg, ArgScan argScan) {
        this.re = re;
        this.localGame = localGame;
        this.sendMsg = sendMsg;
        this.argScan = argScan;
    }

    private ScheduledFuture<?> futureReproducing;

    public boolean rep(Update update) {
        StringBuilder builder = new StringBuilder();

        List<String> args = argScan.s(update);

        if (args.size() > 1 && (args.get(1).contains("file") || args.get(1).contains("db")) ){
            if(localGame.isRegistered()) localGame.setRegistered(false);

            sendMsg.exec(update, builder.append(re.init(args.get(1))));

            return true;
        }
        else{
            sendMsg.exec(update, builder.append("""
                    Use /rep (file/db) to choose type of reproduction source
                    i.e. /rep db
                    
                    Also you can upload your own file
                    To do it use just /rep with attached file
                    """));

            return false;
        }
    }

    public void choose(Update update, CharacterHolder characterHolder){

        List<String> args = argScan.s(update);

        StringBuilder builder= re.reproduce(args.get(0), characterHolder);

        sendMsg.exec(update, builder);
    }
}
