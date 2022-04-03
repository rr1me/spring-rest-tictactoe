package com.example.tictactoe.main.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Service
public class Bot extends TelegramLongPollingBot{

    @Autowired
    @Lazy
    private BotExecutor executor;

    @Override
    public String getBotUsername() {
        return "rimeTTTbot";
    }

    @Override
    public String getBotToken() {
        return "5224652376:AAHWB8w7aGZ1_Zdxs_NJDyAOoSfjf-dUDQ8";
    }

    @Override
    public void onUpdateReceived(Update update) {
//        try {
//            execute(new SendMessage(String.valueOf(update.getMessage().getChatId()), "suka"));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }

        try {
            executor.run(update);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

//        try {
//            sendApiMethod(new SendMessage(update.getMessage().getChatId().toString(), "aya"));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }


//        try {
//            if (selectHandle.getHandle(update) != null)
////                if(update.getMessage().getText().startsWith("/") || botController.isReg())
//                    execute(selectHandle.getHandle(update).process(update).get(0));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }

    }

    public void downloader(String path, File outputFile) throws TelegramApiException {
        downloadFile(path, outputFile);
    }
}
