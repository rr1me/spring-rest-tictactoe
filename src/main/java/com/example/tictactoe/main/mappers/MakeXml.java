package com.example.tictactoe.main.mappers;

import com.example.tictactoe.main.mappers.components.Gameplay;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

@Component
public class MakeXml implements LogFileInterface {

    @Autowired
    private XmlMapper xmlMapper;

    @Override
    public void makeFile(Gameplay gameplay) throws IOException {
        File file = new File("./xrecords/game_1.xml");
        int i = 1;
        while(file.exists()){
            file = new File("./xrecords/game_"+(++i)+".xml");
        }

        xmlMapper.writeValue(file, gameplay);
    }

    @Override
    public Gameplay mapFile(Path path) throws IOException, XMLStreamException {
        String xml = inputStreamToString(new FileInputStream(path.toString()));
        return xmlMapper.readValue(xml, Gameplay.class);
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
