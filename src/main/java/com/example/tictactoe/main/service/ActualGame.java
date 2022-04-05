package com.example.tictactoe.main.service;

import com.example.tictactoe.main.mappers.Logger;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@Scope("prototype")
public class ActualGame {

    private final Logger logger;

    @Autowired
    public ActualGame(Logger logger) {
        this.logger = logger;
    }

    private String[] board = new String[9];
    private int numStep;
    private boolean player;

    private String firstPname;
    private String secondPname;

    private boolean checkWin(String c) {
        if(board[0] == c && board[4] == c && board[8] == c || board[2] == c && board[4] == c && board[6] == c)
            return true;
        for(int i = 0; i < 3; i++){
            if(board[i] == c && board[3+i] == c && board[6+i] == c)
                return true;
        }
        for(int i = 0; i < 7; i += 3){
            if(board[i] == c && board[1+i] == c && board[2+i] == c)
                return true;
        }
        return false;
    }

    public StringBuilder writeBoard(){
        StringBuilder builder = new StringBuilder("|---|---|---|\n");
        for(int i=0;i<7;i += 3){
            builder.append("| " + board[i] + " | " + board[i+1] + " | " + board[i+2] + " |\n");
        }
        builder.append("|---|---|---|\n");
        return builder;
    }

    public void register(String firstPname, String secondPname, CharacterHolder characterHolder){
        logger.gameplayInit(firstPname, secondPname);

        logger.setFormat(!characterHolder.getFileFormat().equals("xml"));

        boardInit();
        numStep = 1;
        player = true;
        this.firstPname = firstPname;
        this.secondPname = secondPname;
    }

    public String getTempPlayerName(){
        return (player ? firstPname : secondPname);
    }

    private String getTempPlayer(){
        return (player? "X" : "O");
    }

    public StringBuilder makeStep(String to, CharacterHolder characterHolder) {
        StringBuilder builder = new StringBuilder();
        String tempChar = getTempPlayer();
        int actualStep = Integer.parseInt(to);

        try{
            Integer.parseInt(board[actualStep-1]);
        }catch (Exception e){
            return builder.append("There's already someone's step, make another decision");
        };

        board[actualStep-1] = tempChar;
        builder.append(writeBoard());
        logger.makeStep(numStep++, (player?1:2), actualStep);

        if(numStep > 5 && checkWin(tempChar)) {
            builder.append("Player " + (player ? "1 -> "+firstPname : "2 -> "+secondPname) + " won as \""+getTempPlayer()+"\"");
            logger.makeResult((player?1:2), (player ? firstPname : secondPname), getTempPlayer());

            characterHolder.setLocalGame(false);
        }else if(numStep > 9){
            builder.append("Draw, gg");
            logger.makeDraw();

            characterHolder.setLocalGame(false);
        }
        else
            player = !player;

        return builder;
    }

    public void boardInit(){
        for(int i=0; i<9; i++){
            board[i] = String.valueOf(i+1);
        }
    }
}
