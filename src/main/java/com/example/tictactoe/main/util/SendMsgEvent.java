package com.example.tictactoe.main.util;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class SendMsgEvent extends ApplicationEvent {
    @Getter
    private final SendMessage message;

    public SendMsgEvent(Object object, SendMessage message) {
        super(object);
        this.message = message;
    }
}
