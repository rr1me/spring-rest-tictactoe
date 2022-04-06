package com.example.tictactoe.main.service;

import com.example.tictactoe.main.mappers.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ActualGameTest {

    @MockBean
    private Logger logger; // it actually does its job - disabling logging and file saving

    @Autowired
    private ActualGame game;

    @Autowired
    private ObjectProvider<CharacterHolder> characterHolderObjectProvider;

    @Test
    void actualGameTest() {
        CharacterHolder characterHolder = characterHolderObjectProvider.getObject();

        characterHolder.setOnlineGame(true);
        game.register("https://i.imgur.com/VTU6JGd.jpg", "https://i.imgur.com/1tbOrsC.jpg", characterHolder);
        for (int i=1; i<8; i += 3){
            System.out.println(game.makeStep(String.valueOf(i), characterHolder));
            if (i!=7)
                System.out.println(game.makeStep(String.valueOf(i+2), characterHolder));
        }
    }

}