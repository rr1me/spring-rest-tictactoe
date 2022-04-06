package com.example.tictactoe.main.service;

import com.example.tictactoe.main.util.SendMsgEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Service
public class Bot extends TelegramLongPollingBot implements ApplicationListener<SendMsgEvent> {

    @Autowired
    private MessageHandler messageHandler;

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) { messageHandler.run(update); }

    public void downloader(String path, File outputFile) throws TelegramApiException {
        downloadFile(path, outputFile);
    }

    @Override
    public void onApplicationEvent(SendMsgEvent event) {
        try {
            execute(event.getMessage());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
