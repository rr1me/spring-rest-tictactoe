package com.example.tictactoe.main.mappers.deserializers;

import com.example.tictactoe.main.mappers.components.GameResult;
import com.example.tictactoe.main.mappers.components.Player;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GameResultDeserializer extends JsonDeserializer<GameResult> {
    @Override
    public GameResult deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.readValueAsTree();
        GameResult result = new GameResult();
        if(node.fieldNames().hasNext() && node.get("draw") == null){
            ObjectMapper mapper = new ObjectMapper();
            Player player = mapper.convertValue(node.get("Player"), Player.class);
            result.setPlayer(player);
        }else if(node.get("draw") != null){
            result.setDraw("Draw");
        }
        else{
            result.setDraw(node.asText());
        }
        return result;
    }
}
