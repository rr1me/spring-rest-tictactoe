package com.example.tictactoe.main.service;

import com.example.tictactoe.main.mappers.FileRepo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Getter
@Setter
public class BotExecutor {

    private Bot bot;
    private ActualGame game;
    private Reproduction re;
    private FileRepo fileRepo;

    @Autowired
    public BotExecutor(Bot bot, ActualGame game, Reproduction re, FileRepo fileRepo) {
        this.bot = bot;
        this.game = game;
        this.re = re;
        this.fileRepo = fileRepo;
    }

    private boolean registered = false;
    private boolean reproducing = false;

    public void run(Update update) throws TelegramApiException {
        Message message = update.getMessage();

        if (message.hasText())
            if (message.getText().startsWith("/")) {
                commandEx(update);
            } else {
                try{
                    if (registered) {
                        bot.execute(step(update));
                    }else if(reproducing){
                        bot.execute(rep(update));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        else if(!message.hasText()){
            String caption = message.getCaption();
            if (caption != null){
                if (caption.contains("/rep"))
                    bot.execute(uploadFile(update));
                else
                    bot.execute(noCmd(update));
            }
        }
    }

    private void commandEx(Update update) {
        String cmd = update.getMessage().getText();
        try{
            if (cmd.contains("/game"))
                bot.execute(game(update));
            else if (cmd.contains("/rep"))
                bot.execute(rep(update));
            else if(cmd.contains("/start"))
                bot.execute(start(update));
            else
                bot.execute(noCmd(update));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private SendMessage noCmd(Update update){
        return new SendMessage(update.getMessage().getChatId().toString(), "There is no such command. Try /start");
    }

    private SendMessage start(Update update){
        return new SendMessage(update.getMessage().getChatId().toString(), "Type /game to play\nType /rep to reproduce the game");
    }

    private SendMessage uploadFile(Update update){

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

        return sendMessage(update, new StringBuilder("File uploaded by path "+path));
    }

    private SendMessage rep(Update update) throws IOException {
        StringBuilder builder = new StringBuilder();

        List<String> args = scan(update);

        if (args.size() > 1 && (args.get(1).contains("file") || args.get(1).contains("db")) ){
            if(registered) registered = false;
            builder.append(re.init(args.get(1)));
        }
        else if(reproducing){
            builder.append(re.reproduce(args.get(0)));
        }
        else{
            builder.append("""
                    Use /rep (file/db) to choose type of reproduction source
                    i.e. /rep db
                    
                    Also you can upload your own file
                    To do it use just /rep with attached file
                    """);
        }

        return sendMessage(update, builder);
    }

    private SendMessage game(Update update){
        StringBuilder builder = new StringBuilder();

        List<String> args = scan(update);

        if (args.size() > 2 && !registered){
            String firstPlayer = args.get(1);
            String secondPlayer = args.get(2);
            builder.append("""
                    Registered
                    Now make your steps
                    
                    """);
            game.register(firstPlayer, secondPlayer);
            builder.append(game.writeBoard());
            registered = !registered;
        }else if(registered){
            builder.append("""
                    Already registered
                    Make your step
                    """);
        }
        else{
            builder.append("""
                    To register the game use
                    /game firstPlayerName(X) secondPlayerName(O)
                    """);
        }

        return sendMessage(update, builder);
    }

    private SendMessage step(Update update) throws IOException {
        if (registered)
            return sendMessage(update, game.makeStep(update.getMessage().getText()));
        else
            return sendMessage(update, new StringBuilder("Use /game to register the game\nUse /rep to reproduce game"));
    }

    private List<String> scan(Update update){
        Scanner scanner = new Scanner(update.getMessage().getText());
        List<String> scannerList = new ArrayList<>();
        while(scanner.hasNext()){
            scannerList.add(scanner.next());
        }
        return scannerList;
    }

    private SendMessage sendMessage(Update update, StringBuilder string){
        return new SendMessage(String.valueOf(update.getMessage().getChatId()), string.toString());
    }
}
