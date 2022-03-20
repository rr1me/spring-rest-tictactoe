package com.example.tictactoe.main.mappers;

import com.example.tictactoe.main.mappers.components.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Logger {
    @Getter
    @Setter
    private boolean format = true;

    private final Gameplay gameplay;
    private final Adapter adapter;
    private final MakeXml makeXml;
    private final MakeJson makeJson;

    @Autowired
    public Logger(Gameplay gameplay, Adapter adapter, MakeXml makeXml, MakeJson makeJson) {
        this.gameplay = gameplay;
        this.adapter = adapter;
        this.makeXml = makeXml;
        this.makeJson = makeJson;
    }

    public void gameplayInit(String firstPlayer, String secondPlayer){
        List<Player> playerList = Arrays.asList(new Player(1, firstPlayer, "X"), new Player(2, secondPlayer, "O"));
        gameplay.setPlayers(playerList);
        gameplay.setGame(new Game(new ArrayList<>()));
        gameplay.setGameResult(new GameResult());
    }

    public void makeResult(int id, String name, String symbol) throws IOException {
        Player player = new Player(id, name, symbol);
        gameplay.getGameResult().setPlayer(player);
        makeFile();
    }

    public void makeDraw() throws IOException {
        gameplay.getGameResult().setDraw("Draw");
        makeFile();
    }

    public Gameplay gameplayRep(Path path) throws IOException, XMLStreamException {
        Matcher m = Pattern.compile("\\W(\\w+)$").matcher(path.toString());
        m.find();
        if(Objects.equals(m.group(1), "xml"))
            return makeXml.mapFile(path);
        else
            return makeJson.mapFile(path);
    }

    public void makeStep(int numStep, int playerId, int actualStep){
        Step step = new Step();
        step.setAdapter(adapter);
        step.setNum(numStep);
        step.setPlayerId(playerId);
        step.setActualStep(String.valueOf(actualStep));
        gameplay.getGame().getSteps().add(step);
    }

    private void makeFile() throws IOException {
        if(format)
            makeJson.makeFile(gameplay);
        else
            makeXml.makeFile(gameplay);
    }
}
