package com.example.tictactoe.main.util;

import com.example.tictactoe.main.service.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SendMsg {

    public SendMessage s(Update update, StringBuilder builder) {
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), builder.toString());
    }

    public SendMessage s(Update update, String string) {
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), string);
    }

    public SendMessage s(Long chatId, StringBuilder builder) {
        return new SendMessage(String.valueOf(chatId), builder.toString());
    }

    public SendMessage s(Long chatId, String string) {
        return new SendMessage(String.valueOf(chatId), string);
    }


    @Autowired
    private Bot bot;

    public void exec(Update chatId, StringBuilder builder) {
        try {
            bot.execute(s(chatId, builder));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void exec(Update chatId, String string) {
        try {
            bot.execute(s(chatId, string));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void exec(Long chatId, StringBuilder builder) {
        try {
            bot.execute(s(chatId, builder));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void exec(Long chatId, String string) {
        try {
            bot.execute(s(chatId, string));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
