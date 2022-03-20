package com.example.tictactoe.main.deserializers;

import com.example.tictactoe.main.mappers.components.Adapter;
import com.example.tictactoe.main.mappers.components.Step;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StepDeserializer extends JsonDeserializer<Step> {

    @Autowired
    private Adapter adapter;

    @Override
    public Step deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.readValueAsTree();
        Step step = new Step();
        step.setAdapter(adapter);
        step.setNum(node.get("num").asInt());
        if(node.get("") == null)
            step.setActualStep(node.get("actualStep").asText());
        else
            step.setActualStep(node.get("").asText());
        step.setPlayerId(node.get("playerId").asInt());
        return step;
    }
}
