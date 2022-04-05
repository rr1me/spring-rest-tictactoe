package com.example.tictactoe.main.tasks;

import com.example.tictactoe.main.service.botServices.RepService;
import com.example.tictactoe.main.util.SendMsg;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class OutOfTimeTask implements Runnable {

    private final SendMsg sendMsg;
    private final RepService repService;

    @Autowired
    public OutOfTimeTask(SendMsg sendMsg, @Lazy RepService repService) {
        this.sendMsg = sendMsg;
        this.repService = repService;
    }

    @Setter
    private Update update;

    @Override
    public void run() {
        outOfTime(update);
    }

    private void outOfTime(Update update) {
//        repService.setReproducing(false);
        sendMsg.exec(update, "You're out of time, try again");
    }
}
