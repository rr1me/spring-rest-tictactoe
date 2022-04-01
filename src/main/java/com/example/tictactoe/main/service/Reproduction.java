package com.example.tictactoe.main.service;

import com.example.tictactoe.main.entities.GameEntity;
import com.example.tictactoe.main.mappers.Logger;
import com.example.tictactoe.main.mappers.components.Gameplay;
import com.example.tictactoe.main.mappers.components.Player;
import com.example.tictactoe.main.mappers.components.Step;
import com.example.tictactoe.main.repos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class Reproduction {

    private final ActualGame game;
    private final Logger logger;
    private final GameRepo gameRepo;

    @Autowired
    public Reproduction(@Lazy ActualGame game, Logger logger, GameRepo gameRepo) {
        this.game = game;
        this.logger = logger;
        this.gameRepo = gameRepo;
    }

    private StringBuilder boardReplay(Gameplay gameplay) {

        game.boardInit();
        String[] board = game.getBoard();
        List<Step> steps = gameplay.getGame().getSteps();

        StringBuilder boardBuilder = new StringBuilder();

        for (Step step : steps) {
            board[step.getActualStep() - 1] = (step.getPlayerId() == 1 ? "X" : "O");
            game.setBoard(board);
            boardBuilder.append(game.writeBoard()+"\n");
        }
        return boardBuilder;
    }

    private Gameplay getGameplay(String to, boolean type, List<Path> paths) throws IOException, XMLStreamException {
        if (type)
            return logger.gameplayRepByDb(to);
        Path path = paths.get(Integer.parseInt(to)-1);
        return logger.gameplayRep(path);
    }

    public StringBuilder init(String to, boolean type) throws IOException, XMLStreamException {
        StringBuilder builder = new StringBuilder();

        List<Path> paths = null;
        if (!type) {
            Stream<Path> streamPaths = Files.walk(Paths.get("./xrecords")).filter(Files::isRegularFile);
            paths = new ArrayList<>(streamPaths.toList());
            paths.sort(Path::compareTo);
        }

        if (to != null) {
            Gameplay gameplay = getGameplay(to, type, paths);
            builder.append(boardReplay(gameplay));
            Player player;
            if (gameplay.getGameResult().getDraw() != null)
                return builder.append("Draw, gg");

            player = gameplay.getGameResult().getPlayer();
            return builder.append("Player " + player.getId() + " -> " + player.getName() + " won as \"" + player.getSymbol() + "\"");
        }

        List<String> response;
        if (type)
            response = gameRepo.findAll().stream().map(GameEntity::toString).toList();
        else
            response = paths.stream().map(x -> x.toString().substring(11)).toList();

        for (int i = 0; i < response.size(); i++) {
            builder.append((i + 1) + " | " + response.get(i)+"\n");
        }
        builder.append("""
                
                To reproduce file use param "to" in link i.e. /rep?type=[your type]&to=1
                Also you can upload your own file by sending post request to /rep/upload with body key "file\"""");

        return builder;
    }
}
