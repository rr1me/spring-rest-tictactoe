package com.example.tictactoe.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class TicTacToeApplication implements CommandLineRunner {

    @Autowired
    private ActualGame game;

    @Override
    public void run(String... args) throws XMLStreamException, IOException {
        game.init();
    }

    public static void main(String[] args)  {
        SpringApplication application = new SpringApplication(TicTacToeApplication.class);
        Properties properties = new Properties();
        properties.setProperty("logging.level.root", "warn");
        application.setDefaultProperties(properties);
        application.run(args);
    }

}
