package com.example.tictactoe.xmlReader.components;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Step {

    private int num;
    private int playerId;
    @JacksonXmlText
    private int actualStep;

    public void setActualStep(String actualStep) {
        Adapter adapter = new Adapter();
        this.actualStep = adapter.go(actualStep);
    }
}
