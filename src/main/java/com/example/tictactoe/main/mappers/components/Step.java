package com.example.tictactoe.main.mappers.components;

import com.example.tictactoe.main.mappers.deserializers.StepDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.*;

@Getter
@Setter
@ToString
@JsonDeserialize(using = StepDeserializer.class)
@NoArgsConstructor
public class Step {

    public Step(int num, int playerId, int actualStep) {
        this.num = num;
        this.playerId = playerId;
        this.actualStep = actualStep;
    }

    @JsonIgnore
    private Adapter adapter;

    @JacksonXmlProperty(isAttribute = true)
    private int num;
    @JacksonXmlProperty(isAttribute = true)
    private int playerId;
    @JacksonXmlText
    private int actualStep;

    public void setActualStep(String actualStep) {
        this.actualStep = adapter.go(actualStep);
    }
}
