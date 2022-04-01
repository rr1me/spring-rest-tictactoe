package com.example.tictactoe.main.service;

import com.example.tictactoe.main.mappers.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

@SpringBootTest
class ActualGameTest {

    @MockBean
    private Logger logger; // it actually does its job - disabling logging and file saving

    @Autowired
    private ActualGame game;

    @Test
    void actualGameTest() throws IOException {
        game.register("https://i.imgur.com/VTU6JGd.jpg", "https://i.imgur.com/1tbOrsC.jpg");
        for (int i=1; i<8; i += 3){
            System.out.println(game.makeStep(String.valueOf(i)));
            if (i!=7)
                System.out.println(game.makeStep(String.valueOf(i+2)));
        }
    }

}