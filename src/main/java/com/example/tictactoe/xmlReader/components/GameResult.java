package com.example.tictactoe.xmlReader.components;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GameResult {
    @JacksonXmlProperty(localName = "Player")
    private Player player;
}
