package com.example.tictactoe.main.mappers.components;

import com.example.tictactoe.main.deserializers.StepDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@ToString
@JsonDeserialize(using = StepDeserializer.class)
public class Step {

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
