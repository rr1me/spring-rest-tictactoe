package com.example.tictactoe.main.mappers;

import com.example.tictactoe.main.entities.GameEntity;
import com.example.tictactoe.main.entities.StepEntity;
import com.example.tictactoe.main.mappers.components.*;
import com.example.tictactoe.main.repos.GameRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private final GameRepo gameRepo;

    @Autowired
    public Logger(Gameplay gameplay, Adapter adapter, MakeXml makeXml, MakeJson makeJson, GameRepo gameRepo) {
        this.gameplay = gameplay;
        this.adapter = adapter;
        this.makeXml = makeXml;
        this.makeJson = makeJson;
        this.gameRepo = gameRepo;
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
        makeFile(id);
    }

    public void makeDraw() throws IOException {
        gameplay.getGameResult().setDraw("Draw");
        makeFile(3);
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

    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public Gameplay gameplayRepByDb(String to){
        GameEntity gameEntity = gameRepo.findById(Long.parseLong(to));

        List<Player> players = Arrays.asList(new Player(1, gameEntity.getFirstPlayer(), "X"), new Player(2, gameEntity.getSecondPlayer(), "O"));

        GameResult gameResult = new GameResult();
        switch (gameEntity.getWinner()){
            case 1 -> gameResult.setPlayer(new Player(1, gameEntity.getFirstPlayer(), "X"));
            case 2 -> gameResult.setPlayer(new Player(2, gameEntity.getSecondPlayer(), "O"));
            case 3 -> gameResult.setDraw("Draw");
        }

        Game game = new Game(new ArrayList<>());
        for (StepEntity stepEntity : gameEntity.getSteps()){
            game.getSteps().add(new Step(stepEntity.getNum(), stepEntity.getPlayerId(), stepEntity.getActualStep()));
        }

        gameplay.setPlayers(players);
        gameplay.setGameResult(gameResult);
        gameplay.setGame(game);
        return gameplay;
    }

    private void makeFile(int winner) throws IOException {

        List<String> playerNames = gameplay.getPlayers().stream().map(Player::getName).toList();

        GameEntity gameEntity = new GameEntity(playerNames.get(0), playerNames.get(1), winner);

        List<StepEntity> stepEntities = new ArrayList<>();
        for (Step step : gameplay.getGame().getSteps()){
            stepEntities.add(new StepEntity(step.getNum(), step.getPlayerId(), step.getActualStep(), gameEntity));
        }
        gameEntity.setSteps(stepEntities);

        gameRepo.save(gameEntity);


        if(format)
            makeJson.makeFile(gameplay);
        else
            makeXml.makeFile(gameplay);
    }
}
