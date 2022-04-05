package com.example.tictactoe.main.service.botServices;

import com.example.tictactoe.main.mappers.FileRepo;
import com.example.tictactoe.main.service.Bot;
import com.example.tictactoe.main.service.CharacterHolder;
import com.example.tictactoe.main.util.ArgScan;
import com.example.tictactoe.main.util.SendMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileHandler {

    private final Bot bot;
    private final SendMsg sendMsg;
    private final ArgScan argScan;
    private final FileRepo fileRepo;

    @Autowired
    public FileHandler(@Lazy Bot bot, SendMsg sendMsg, ArgScan argScan, FileRepo fileRepo) {
        this.bot = bot;
        this.sendMsg = sendMsg;
        this.argScan = argScan;
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

    public void changeFormat(Update update, CharacterHolder characterHolder){

        List<String> args = argScan.s(update);

        if (args.size() > 1 && (args.get(1).equals("xml") || args.get(1).equals("json")) ){
            characterHolder.setFileFormat(args.get(1));
            sendMsg.exec(update, "Format has been changed to: "+args.get(1));
        }else
            sendMsg.exec(update, "Example: /format xml. Supported formats: xml, json");
    }
}
