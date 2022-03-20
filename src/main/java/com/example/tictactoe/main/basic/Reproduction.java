package com.example.tictactoe.main.basic;

import com.example.tictactoe.main.mappers.Logger;
import com.example.tictactoe.main.mappers.components.Gameplay;
import com.example.tictactoe.main.mappers.components.Player;
import com.example.tictactoe.main.mappers.components.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class Reproduction {

    private final ActualGame game;
    private final Logger logger;

    @Autowired
    public Reproduction(@Lazy ActualGame game, Logger logger) {
        this.game = game;
        this.logger = logger;
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

    public StringBuilder init(String to) throws IOException, XMLStreamException {

        StringBuilder builder = new StringBuilder();
        Supplier<Stream<Path>> paths = () -> {
            try {
                return Files.walk(Paths.get("./xrecords")).filter(Files::isRegularFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };

        if(to != null){
            Path path = paths.get().toList().get(Integer.parseInt(to)-1);
            Gameplay gameplay = logger.gameplayRep(path);
            builder.append(boardReplay(gameplay));
            Player player;
            if (gameplay.getGameResult().getDraw() != null){
                builder.append("Draw, gg");
            }
            else {
                player = gameplay.getGameResult().getPlayer();
                builder.append("Player "+player.getId()+" -> "+player.getName()+" won as \"" +player.getSymbol()+ "\"");
            }
        }
        else{
            for (int i = 0; i < paths.get().count(); i++){
                String v = paths.get().toList().get(i).toString().substring(11);
                builder.append((i+1)+" | "+v+"\n");
            }
            builder.append("""
                        To reproduce file use param "to" in link i.e. /rep?to=1
                        Also you can upload your own file by sending post request to /rep/upload with body key "file\"""");
        }

        return builder;
    }
}
