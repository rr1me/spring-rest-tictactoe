package com.example.tictactoe.main.mappers;

import com.example.tictactoe.main.entities.*;
import com.example.tictactoe.main.mappers.components.*;
import com.example.tictactoe.main.repos.GameRepo;
import com.example.tictactoe.main.repos.PlayerRepo;
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
    private final PlayerRepo playerRepo;

    @Autowired
    public Logger(Gameplay gameplay, Adapter adapter, MakeXml makeXml, MakeJson makeJson, GameRepo gameRepo, PlayerRepo playerRepo) {
        this.gameplay = gameplay;
        this.adapter = adapter;
        this.makeXml = makeXml;
        this.makeJson = makeJson;
        this.gameRepo = gameRepo;
        this.playerRepo = playerRepo;
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


        Player firstPlayer = new Player(1, gameEntity.getFirstPlayer(), "X");
        Player secondPlayer = new Player(2, gameEntity.getSecondPlayer(), "O");
        List<Player> players = Arrays.asList(firstPlayer, secondPlayer);

        GameResult gameResult = new GameResult();
        switch (gameEntity.getWinner()){
            case 1 -> gameResult.setPlayer(firstPlayer);
            case 2 -> gameResult.setPlayer(secondPlayer);
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

    private PlayerEntity findPlayer(String name){
        PlayerEntity player = playerRepo.findByName(name);
        if (player == null)
            player = new PlayerEntity(name, 0, 0, 0);
        return player;
    }

    @Transactional(propagation= Propagation.REQUIRED)
    public void makeFile(int winner) throws IOException {

        List<String> playerNames = gameplay.getPlayers().stream().map(Player::getName).toList();

        PlayerEntity firstPlayerEntity = findPlayer(playerNames.get(0));
        PlayerEntity secondPlayerEntity = findPlayer(playerNames.get(1));


        switch (winner){
            case 1 -> {
                firstPlayerEntity.changeStats("win");
                secondPlayerEntity.changeStats("loss");
            }
            case 2 -> {
                secondPlayerEntity.changeStats("win");
                firstPlayerEntity.changeStats("loss");
            }
            case 3 -> {
                firstPlayerEntity.changeStats("draw");
                secondPlayerEntity.changeStats("draw");
            }
        }
//        playerRepo.save(firstPlayerEntity);
//        playerRepo.save(secondPlayerEntity);
//        System.out.println(firstPlayerEntity.getName()+" "+secondPlayerEntity.getName()+" entities");

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
