package com.example.tictactoe.main.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviveController {

    @GetMapping
    public String revive(){
        return "Revived, now bot is online";
    }
}
