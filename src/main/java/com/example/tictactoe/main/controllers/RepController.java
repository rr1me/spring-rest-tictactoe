package com.example.tictactoe.main.controllers;

import com.example.tictactoe.main.service.Reproduction;
import com.example.tictactoe.main.mappers.FileRepo;
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
@RequestMapping("/gameplay/rep")
public class RepController {

    private final Reproduction re;
    private final FileRepo fileRepo;
    private final GameController gameController;

    @Autowired
    public RepController(Reproduction re, FileRepo fileRepo, GameController gameController) {
        this.re = re;
        this.fileRepo = fileRepo;
        this.gameController = gameController;
    }

    @GetMapping
    public StringBuilder indexRep(@RequestParam(required = false) String type, @RequestParam(required = false) String to) throws XMLStreamException, IOException {
        if (type != null){
            if(gameController.isReg()) gameController.setReg(false);
            return re.init(to, Boolean.parseBoolean(type));
        }
        return new StringBuilder("Use param \"type\" to choose type of reproduction source, i.e. /rep?type=false. false=file|true=database");
    }

    @PostMapping("/upload")
    public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file){
        try {
            StringBuilder path = new StringBuilder(fileRepo.getBasePath() + file.getOriginalFilename());
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

}
