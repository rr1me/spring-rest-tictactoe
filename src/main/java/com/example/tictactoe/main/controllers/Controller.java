package com.example.tictactoe.main.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping(value = {"", "/gameplay"})
    public String index(){
        return """
                Game: /gameplay/game
                Reproduction: /gameplay/rep""";
    }
}
