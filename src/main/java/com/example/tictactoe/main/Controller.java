package com.example.tictactoe.main;

import com.example.tictactoe.main.basic.ActualGame;
import com.example.tictactoe.main.basic.Reproduction;
import com.example.tictactoe.main.mappers.Logger;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Setter
public class Controller {

    private ActualGame actualGame;
    private Reproduction re;
    private Logger logger;

    @Autowired
    public Controller(ActualGame actualGame, Reproduction re, Logger logger) {
        this.actualGame = actualGame;
        this.re = re;
        this.logger = logger;
    }

    private boolean reg = false;

    @GetMapping
    public String index(){
        return """
                Game: /game
                Reproduction: /rep""";
    }

    @GetMapping("/rep")
    public StringBuilder rep(@RequestParam(required = false) String to) throws XMLStreamException, IOException {
        if(reg) reg = false;
        return re.init(to);
    }

    @PostMapping("/rep/upload")
    public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file){
        try {
            StringBuilder path = new StringBuilder("./xrecords/" + file.getOriginalFilename());
            File fileCheck = new File(path.toString());
            if(fileCheck.exists()){
                Pattern pBrace = Pattern.compile(".*(\\(\\d\\)\\.).*");
                Matcher m = Pattern.compile("\\.\\w").matcher(path);
                int i = 1;
                m.find();
                while(fileCheck.exists()){
                    Matcher mBrace = pBrace.matcher(path);
                    if(mBrace.matches())
                        path.setCharAt(mBrace.start(1)+1, String.valueOf(i++).charAt(0));
                    else
                        path.insert(m.start(), "(" +(i++)+ ")");
                    fileCheck = new File(path.toString());
                }
            }

            byte[] bytes = file.getBytes();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path.toString()));
            stream.write(bytes);
            stream.close();
            return "File uploaded by path "+path;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/game")
    public StringBuilder game(@RequestParam(required = false) String firstPlayer, @RequestParam(required = false) String secondPlayer, @RequestParam(required = false) String format){
        StringBuilder string;
        if (firstPlayer != null && secondPlayer != null && !reg){
            string = new StringBuilder("""
                    Registered
                    Go to the /game/step to make steps
                    """);
            actualGame.register(firstPlayer, secondPlayer);
            reg = !reg;
        }else if(reg){
            string = new StringBuilder("""
                    Already registered
                    Go to the /game/step to make steps
                    """);
        }
        else{
            string = new StringBuilder("""
                    To register the game type names of players in params: firstPlayer(X), secondPlayer(O)
                    i.e. /game?firstPlayer=Aya&secondPlayer=Eve
                    """);
        }
        if (format != null){
            logger.setFormat(Boolean.parseBoolean(format));
            string.append("Format has been changed to "+ (logger.isFormat()? "json" : "xml") );
        }else
            string.append("Or change the format of the recording file by using param \"format\" i.e. /game?format=false. false"+ (logger.isFormat()? "=xml|true(now)" : "(now)=xml|true") +"=json");

        return string;
    }

    @GetMapping("/game/step")
    public StringBuilder step(@RequestParam(required = false) String to) throws IOException {
        StringBuilder builder = new StringBuilder();

        if(reg)
            builder.append(actualGame.makeStep(to));
        else
            builder.append("Need to register the game in /game");

        return builder;
    }
}
