package com.example.tictactoe.main.configs;

import com.example.tictactoe.main.service.Bot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ComponentScan("com.example.tictactoe")
public class SpringConfig {

    @Autowired
    private ApplicationContext context;

    @Bean
    public BotSession botSession() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = (Bot) context.getBean("bot");
        return botsApi.registerBot(bot);
    }

    private HandlerInstantiator handlerInstantiator(ApplicationContext applicationContext) {
        return new SpringHandlerInstantiator(applicationContext.getAutowireCapableBeanFactory());
    }

    private Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.handlerInstantiator(handlerInstantiator(context));
        return builder;
    }

    @Bean
    public XmlMapper xmlMapper(){
        XmlMapper mapper = objectMapperBuilder().createXmlMapper(true).build();
        mapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = objectMapperBuilder().createXmlMapper(false).build();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
}