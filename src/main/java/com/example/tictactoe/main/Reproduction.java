package com.example.tictactoe.main;

import com.example.tictactoe.xmlReader.Gameplay;
import com.example.tictactoe.xmlReader.components.Step;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
public class Reproduction {

    private final ActualGame game;
    private final BufferedReader reader;
    private Gameplay gameplay;
    private XmlMapper mapper;

    @Autowired
    public Reproduction(@Lazy ActualGame game, BufferedReader reader, Gameplay gameplay, XmlMapper mapper) {
        this.game = game;
        this.reader = reader;
        this.gameplay = gameplay;
        this.mapper = mapper;
    }

    private Supplier<Stream<Path>> paths;

    private File getFile() throws IOException {
        File file = null;

        if(paths.get().findAny().isEmpty()){
            System.out.println("There is no xml files. Play new game or put your old file inside /xml folder.");
            System.exit(-1);
        }
        else if(paths.get().count()>1){
            for (int i = 0; i < paths.get().count(); i++){
                String v = paths.get().toList().get(i).toString().substring(6);
                System.out.println((i+1)+" | "+v);
            }
            System.out.println("Multiple files detected. Type the number of the file you want to use");
            file = new File(paths.get().toList().get(getIndex()).toString());
        }
        else file = new File(paths.get().toList().get(0).toString());

        return file;
    }

    private int getIndex() throws IOException {
        String input = reader.readLine();
        int i;
        if(input.contains("stop")){
            System.exit(1);
        }
        try{
            i = Integer.parseInt(input)-1;
            if(i < 0 || i >= paths.get().toList().size()){
                System.out.println("Wrong number, try again.");
                i = getIndex();
            }
        }catch (NumberFormatException e){
            System.out.println("Its not a number, try again.");
            i = getIndex();
        }
        return i;
    }

    private void boardReplay() throws IOException, XMLStreamException {
        mapper = new XmlMapper();
        String xml = inputStreamToString(new FileInputStream(getFile()));
        gameplay = mapper.readValue(xml, Gameplay.class);

        game.boardInit();
        String[] board = game.getBoard();
        List<Step> steps = gameplay.getGame().getSteps();


        for (Step step : steps) {
            System.out.println(step);
            board[step.getActualStep() - 1] = (step.getPlayerId() == 1 ? "X" : "O");
            game.setBoard(board);
            game.writeBoard();
            System.out.println();
        }
    }

    public void init() throws IOException, XMLStreamException {

        paths = () -> {
            try {
                return Files.walk(Paths.get("./xml")).filter(Files::isRegularFile).filter(x->x.getFileName().toString().contains(".xml"));
            } catch (IOException e) {
                System.out.println("There is no xml folder. It was created, now put the xml game files inside and rerun program.");
                File f = new File("./xml");
                f.mkdir();
                System.exit(-1);
            }
            return null;
        };

        boardReplay();

        if(gameplay.getGameResult() == null){
            System.out.println("Game wasn't over.");
        }
        else if(gameplay.getGameResult().getClass().toString().contains("String"))
            System.out.println("Draw, gg.");
        else{
            LinkedHashMap<String, LinkedHashMap<String, String>> map = (LinkedHashMap) gameplay.getGameResult();
            List<String> player = map.values().stream().findFirst().get().values().stream().toList();
            System.out.println("Player "+player.get(0)+" -> "+player.get(1)+" is winner as \""+player.get(2)+"\".");
        }

    }

    private String inputStreamToString(InputStream is) throws IOException, XMLStreamException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        is.transferTo(baos);
        InputStream toUse = new ByteArrayInputStream(baos.toByteArray());
        InputStream toCheck = new ByteArrayInputStream(baos.toByteArray());
        XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(toCheck);
        String fileEncoding = xmlStreamReader.getCharacterEncodingScheme();

        String encoding;
        if (Objects.equals(fileEncoding, "UTF-8"))
            encoding = "UTF8";
        else
            encoding = "Windows-1251";

        StringBuilder sb = new StringBuilder();
        String line;
        InputStreamReader streamReader = new InputStreamReader(toUse, encoding);
        BufferedReader br = new BufferedReader(streamReader);
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
