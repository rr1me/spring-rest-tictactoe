package com.example.tictactoe.main.controllers;

import com.example.tictactoe.main.mappers.Logger;
import com.example.tictactoe.main.service.ActualGame;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Getter
@Setter
@RequestMapping("/gameplay")
public class GameController {

    private final ActualGame actualGame;
    private final Logger logger;

    @Autowired
    public GameController(ActualGame actualGame, Logger logger) {
        this.actualGame = actualGame;
        this.logger = logger;
    }

    private boolean reg = false;

    @GetMapping("/game")
    public StringBuilder game(@RequestParam(required = false) String firstPlayer, @RequestParam(required = false) String secondPlayer, @RequestParam(required = false) String format){
        StringBuilder string;
        if (firstPlayer != null && secondPlayer != null && !reg){
            string = new StringBuilder("""
                    Registered
                    Go to the /game/step to make steps
                    """);
            actualGame.register(firstPlayer, secondPlayer);
            reg = !reg;
        }else if(reg){
            string = new StringBuilder("""
                    Already registered
                    Go to the /game/step to make steps
                    """);
        }
        else{
            string = new StringBuilder("""
                    To register the game type names of players in params: firstPlayer(X), secondPlayer(O)
                    i.e. /game?firstPlayer=Aya&secondPlayer=Eve
                    """);
        }
        if (format != null){
            logger.setFormat(Boolean.parseBoolean(format));
            string.append("Format has been changed to "+ (logger.isFormat()? "json" : "xml") );
        }else
            string.append("Or change the format of the recording file by using param \"format\" i.e. /game?format=false. false"+ (logger.isFormat()? "=xml|true(now)" : "(now)=xml|true") +"=json");

        return string;
    }

    @GetMapping("/game/step")
    public StringBuilder step(@RequestParam(required = false) String to) throws IOException {
        StringBuilder builder = new StringBuilder();

        if(reg)
            builder.append(actualGame.makeStep(to));
        else
            builder.append("Need to register the game in /game");

        return builder;
    }
}
