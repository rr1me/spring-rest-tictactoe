package com.example.tictactoe.main.service.botServices;

import com.example.tictactoe.main.mappers.FileRepo;
import com.example.tictactoe.main.service.Bot;
import com.example.tictactoe.main.util.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileHandler {

    private final Bot bot;
    private final SendMsg sendMsg;
    private final FileRepo fileRepo;

    @Autowired
    public FileHandler(Bot bot, SendMsg sendMsg, FileRepo fileRepo) {
        this.bot = bot;
        this.sendMsg = sendMsg;
        this.fileRepo = fileRepo;
    }

    public void upload(Update update){

        Document document = update.getMessage().getDocument();
        GetFile getFile = new GetFile(document.getFileId());
        String filePath;

        StringBuilder path = new StringBuilder(fileRepo.getBasePath() + "\\" + document.getFileName());
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
        try{
            filePath = bot.execute(getFile).getFilePath();
            bot.downloader(filePath, fileCheck);
        }catch (TelegramApiException e){
            e.printStackTrace();
        }

        sendMsg.exec(update, "File uploaded by path "+path);
    }
}
