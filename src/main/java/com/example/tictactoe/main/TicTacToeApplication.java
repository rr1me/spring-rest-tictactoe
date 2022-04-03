package com.example.tictactoe.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TicTacToeApplication {


    public static void main(String[] args)  {
        SpringApplication application = new SpringApplication(TicTacToeApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);

//        SpringApplication.run(TicTacToeApplication.class, args);
    }

}
