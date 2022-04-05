package com.example.tictactoe.main.service;

import com.example.tictactoe.main.entities.GameEntity;
import com.example.tictactoe.main.exceptions.OutOfBoundsExcp;
import com.example.tictactoe.main.mappers.Logger;
import com.example.tictactoe.main.mappers.components.Gameplay;
import com.example.tictactoe.main.mappers.components.Player;
import com.example.tictactoe.main.mappers.components.Step;
import com.example.tictactoe.main.repos.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

@Service
@Scope("prototype")
public class Reproduction {

    private final ActualGame game;
    private final Logger logger;
    private final GameRepo gameRepo;

    @Autowired
    public Reproduction(ActualGame game, Logger logger, GameRepo gameRepo) {
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
    private String type;
    private List<Path> paths;

    private Gameplay getGameplay(String to, String type, List<Path> paths) throws IOException, XMLStreamException, OutOfBoundsExcp {
        int i = Integer.parseInt(to);

        if (type.equals("file") && i > paths.size())
            throw new OutOfBoundsExcp();
        else{
            if (type.equals("db"))
                return logger.gameplayRepByDb(to);
            Path path = paths.get(Integer.parseInt(to)-1);
            return logger.gameplayRep(path);
        }

    }

    public StringBuilder reproduce(String to, ScheduledFuture<?> futureReproducing) {
        StringBuilder builder = new StringBuilder();

        Gameplay gameplay;
        try{
            gameplay = getGameplay(to, type, paths);
        }catch (NumberFormatException e){
            return builder.append("Its not a number");
        }catch (OutOfBoundsExcp | IOException | XMLStreamException e){
            return builder.append(e.getMessage());
        }

        futureReproducing.cancel(true);

//        repService.setReproducing(false);

        builder.append(boardReplay(gameplay));
        Player player;
        if (gameplay.getGameResult().getDraw() != null)
            return builder.append("Draw, gg");

        player = gameplay.getGameResult().getPlayer();
        return builder.append("Player " + player.getId() + " -> " + player.getName() + " won as \"" + player.getSymbol() + "\"");
    }

    public StringBuilder init(String type) throws IOException {
        StringBuilder builder = new StringBuilder();

        List<String> response;
        if (type.contains("file")) {
            Stream<Path> streamPaths = Files.walk(Paths.get("./xrecords")).filter(Files::isRegularFile);
            paths = new ArrayList<>(streamPaths.toList());
            paths.sort(Path::compareTo);
            response = paths.stream().map(x -> x.toString().substring(11)).toList();
        }
        else
            response = gameRepo.findAll().stream().map(GameEntity::toString).toList();

        this.type = type;

        for (int i = 0; i < response.size(); i++) {
            builder.append((i + 1) + " | " + response.get(i)+"\n");
        }
//        repService.setReproducing(true);

        builder.append("""
                
                Now type game number you want to reproduce""");

        return builder;
    }
}
