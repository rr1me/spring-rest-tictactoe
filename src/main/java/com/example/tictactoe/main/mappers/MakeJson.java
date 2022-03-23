package com.example.tictactoe.main.mappers;

import com.example.tictactoe.main.mappers.components.Gameplay;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Component
public class MakeJson implements LogFileInterface{

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void makeFile(Gameplay gameplay) throws IOException {
        File file = new File("./xrecords/game_1.json");
        int i = 1;
        while(file.exists()){
            file = new File("./xrecords/game_"+(++i)+".json");
        }

        objectMapper.writeValue(file, gameplay);
    }

    @Override
    public Gameplay mapFile(Path path) throws IOException {
        return objectMapper.readValue(new File(path.toString()), Gameplay.class);
    }
}
