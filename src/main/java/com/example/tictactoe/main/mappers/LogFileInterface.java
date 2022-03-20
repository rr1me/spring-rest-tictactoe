package com.example.tictactoe.main.mappers;

import com.example.tictactoe.main.mappers.components.Gameplay;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;

public interface LogFileInterface {
    void makeFile(Gameplay gameplay) throws IOException;
    Gameplay mapFile(Path path) throws IOException, XMLStreamException;
}
