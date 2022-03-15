package com.example.tictactoe.main;

import com.example.tictactoe.xmlWriter.MyXML;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.IOException;

@Service
@Getter
@Setter
public class ActualGame {

    private final BufferedReader reader;
    private final MyXML xml;
    private final Reproduction reproduction;

    @Autowired
    public ActualGame(BufferedReader reader, MyXML xml, Reproduction reproduction) {
        this.reader = reader;
        this.xml = xml;
        this.reproduction = reproduction;
    }

    private String[] board = new String[9];
    private int numStep = 1;
    private boolean player = true;

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

    public void writeBoard(){
        System.out.println("|---|---|---|");
        for(int i=0;i<7;i += 3){
            System.out.println("| " + board[i] + " | " + board[i+1] + " | " + board[i+2] + " |");
        }
        System.out.println("|---|---|---|");
    }

    private void makeStep() throws IOException, XMLStreamException {

        String input = reader.readLine();

        if(input.contains("stop")){
            xml.game2xmlEnd();
            xml.xmlEnd();
            System.exit(1);
        }

        int actualStep;

        try{
            actualStep = Integer.parseInt(input);
            if(actualStep < 1 || actualStep > 9){
                System.out.println("The cell number can't be less than 1 and more than 9.");
                makeStep();
            }
            else if(Character.isLetter(board[actualStep-1].charAt(0))) {
                System.out.println("There is already "+board[actualStep-1]+". Make another decision.");
                makeStep();
            }
            else {
                board[actualStep - 1] = (player ? "X" : "O");
                xml.game2xmlStep(String.valueOf(numStep++), (player?"1":"2"), String.valueOf(actualStep));
            }
        }catch (NumberFormatException e){
            System.out.println("Its not a number, try again.");
            makeStep();
        }
    }

    private void play() throws IOException, XMLStreamException {

        writeBoard();

        makeStep();

        if(numStep > 4 && checkWin(player?"X":"O")){
            writeBoard();
            System.out.println("Player "+(player?firstPname:secondPname)+" wins, congrats.");
            xml.game2xmlEnd();
            xml.result2xml((player ? "1" : "2"), (player?firstPname:secondPname), (player?"X":"O"));
            xml.xmlEnd();
        }else if(numStep > 9){
            System.out.println("Draw, gg.");
            xml.game2xmlEnd();
            xml.result2xmlDraw();
            xml.xmlEnd();
        }else{
            player = !player;
            play();
        }

    }

    public void boardInit(){
        for(int i=0; i<9; i++){
            board[i] = String.valueOf(i+1);
        }
    }

    private void playOrReplay() throws IOException, XMLStreamException {
        System.out.println("Type \"play\" if you want to... ehm... play... Or you can type \"reproduce\" to play back old game which was recorded in XML file. Also you can always type \"stop\" to end the program.");
        String input = reader.readLine();

        if(input.contains("reproduce")){
            reproduction.init();
        }
        else if(input.contains("play")){

            boardInit();
            xml.xmlGo();

            System.out.println("Type name of the first player which is going to play under \"X\".");
            firstPname = reader.readLine();
            xml.player2xml("1", firstPname, "X");

            System.out.println("Ok, now type name of the second player which is going to play under \"O\".");
            secondPname = reader.readLine();
            xml.player2xml("2", secondPname, "O");

            System.out.println("Game started, good luck.");

            xml.game2xmlStart();
            play();

        }
        else if(input.contains("stop")){
            System.exit(1);
        }
        else { System.out.println("There is no such command"); playOrReplay(); }
    }

    public void init() throws IOException, XMLStreamException {
        System.out.println("Hi, that is simple TTT game with xml recording.");
        playOrReplay();
    }

}
