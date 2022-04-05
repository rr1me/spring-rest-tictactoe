package com.example.tictactoe.main.service.botServices;

import com.example.tictactoe.main.service.Reproduction;
import com.example.tictactoe.main.tasks.OutOfTimeTask;
import com.example.tictactoe.main.util.ArgScan;
import com.example.tictactoe.main.util.SendMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Service
@Setter
@Getter
public class RepService {

    private final Reproduction re;
    private final LocalGame localGame;

    private final TaskScheduler scheduler;
    private final OutOfTimeTask outOfTimeTask;

    private final SendMsg sendMsg;
    private final ArgScan argScan;

    @Autowired
    public RepService(Reproduction re, LocalGame localGame, TaskScheduler scheduler, OutOfTimeTask outOfTimeTask, SendMsg sendMsg, ArgScan argScan) {
        this.re = re;
        this.localGame = localGame;
        this.scheduler = scheduler;
        this.outOfTimeTask = outOfTimeTask;
        this.sendMsg = sendMsg;
        this.argScan = argScan;
    }

    private boolean reproducing = false;
    private ScheduledFuture<?> futureReproducing;

    public void rep(Update update) throws IOException {
        StringBuilder builder = new StringBuilder();

        List<String> args = argScan.s(update);

        if (args.size() > 1 && (args.get(1).contains("file") || args.get(1).contains("db")) ){
            if(localGame.isRegistered()) localGame.setRegistered(false);
            builder.append(re.init(args.get(1)));
            reproducing = true;

            if (futureReproducing != null)
                futureReproducing.cancel(true);

            outOfTimeTask.setUpdate(update);
            futureReproducing = scheduler.schedule(outOfTimeTask, new Date(System.currentTimeMillis() + 10000));
        }
        else if(reproducing){
            StringBuilder builder1= re.reproduce(args.get(0), futureReproducing);

            if (builder1.toString().contains("won as") || builder1.toString().contains("Draw"))
                reproducing = false;

            builder.append(builder1);
        }
        else{
            builder.append("""
                    Use /rep (file/db) to choose type of reproduction source
                    i.e. /rep db
                    
                    Also you can upload your own file
                    To do it use just /rep with attached file
                    """);
        }

        sendMsg.exec(update, builder);
    }
}
