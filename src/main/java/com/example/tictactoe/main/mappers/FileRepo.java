package com.example.tictactoe.main.mappers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Getter
@Setter
public class FileRepo {

    private Path basePath = Path.of("./xrecords/");
}