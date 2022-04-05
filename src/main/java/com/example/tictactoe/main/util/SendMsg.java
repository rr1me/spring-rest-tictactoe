package com.example.tictactoe.main.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
    private ApplicationEventPublisher publisher;

    public void exec(Update chatId, StringBuilder builder) {
        publisher.publishEvent(new SendMsgEvent(this, s(chatId, builder)));
    }

    public void exec(Update chatId, String string) {
        publisher.publishEvent(new SendMsgEvent(this, s(chatId, string)));
    }

    public void exec(Long chatId, StringBuilder builder) {
        publisher.publishEvent(new SendMsgEvent(this, s(chatId, builder)));
    }

    public void exec(Long chatId, String string) {
        publisher.publishEvent(new SendMsgEvent(this, s(chatId, string)));
    }
}
