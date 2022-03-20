package com.example.tictactoe.main.mappers.components;

import com.example.tictactoe.main.deserializers.GameResultDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonUnwrapped
    private List<Player> players;

    @JacksonXmlProperty(localName = "Game")
    private Game game;

    @JacksonXmlProperty(localName = "GameResult")
    @JsonDeserialize(using = GameResultDeserializer.class)
    private GameResult gameResult;
}

