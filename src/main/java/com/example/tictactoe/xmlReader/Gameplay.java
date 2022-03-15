package com.example.tictactoe.xmlReader;

import com.example.tictactoe.xmlReader.components.Game;
import com.example.tictactoe.xmlReader.components.Player;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Gameplay {

    @JacksonXmlProperty(localName = "Player")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Player> players;

    @JacksonXmlProperty(localName = "Game")
    private Game game;

    @JacksonXmlProperty(localName = "GameResult")
    private Object gameResult;
}

