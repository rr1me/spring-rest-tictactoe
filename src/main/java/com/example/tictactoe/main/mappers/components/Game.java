package com.example.tictactoe.main.mappers.components;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @JacksonXmlProperty(localName = "Step")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Step> steps = new ArrayList<>();
}
