package com.example.tictactoe.xmlWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@Service
public class MyXML {

    private final XMLOutputFactory outputFactory;

    private final XMLEventFactory factory;

    private XMLEventWriter writer;

    private final Characters end;
    private final Characters tab;

    @Autowired
    public MyXML(XMLOutputFactory outputFactory, XMLEventFactory factory, XMLEventWriter writer) {
        this.outputFactory = outputFactory;
        this.factory = factory;
        this.writer = writer;

        end = factory.createCharacters("\n");
        tab = factory.createCharacters("\t");
    }

    private void fileInit() throws FileNotFoundException, XMLStreamException {
        File file = new File("./xml/game_1.xml");
        int i = 1;
        while(file.exists()){
            file = new File("./xml/game_"+(++i)+".xml");
        }
        writer = outputFactory.createXMLEventWriter(new FileOutputStream(file));
    }

    public void player2xml(String id, String name, String symbol) throws XMLStreamException {
        add(end);
        add(tab);

        add(createStartElement("Player"));

        add(factory.createAttribute("id", id));
        add(factory.createAttribute("name", name));
        add(factory.createAttribute("symbol", symbol));

        add(createEndElement("Player"));
    }

    public void xmlGo() throws FileNotFoundException, XMLStreamException {
        fileInit();

        add(factory.createStartDocument());
        add(end);
        add(createStartElement("Gameplay"));
    }

    public void xmlEnd() throws XMLStreamException {
        add(end);
        add(createEndElement("Gameplay"));

        writer.flush();
        writer.close();
    }

    public void game2xmlStart() throws XMLStreamException {
        add(end);
        add(tab);

        add(createStartElement("Game"));
    }

    public void game2xmlEnd() throws XMLStreamException {
        add(end);
        add(tab);

        add(createEndElement("Game"));
    }

    public void game2xmlStep(String numStep, String playerId, String actualStep) throws XMLStreamException {
        add(end);
        add(tab);
        add(tab);

        add(createStartElement("Step"));
        add(factory.createAttribute("num", numStep));
        add(factory.createAttribute("playerId", playerId));
        add(factory.createCharacters(actualStep));
        add(createEndElement("Step"));
    }

    public void result2xml(String id, String name, String symbol) throws XMLStreamException {
        add(end);
        add(tab);

        add(createStartElement("GameResult"));
        add(createStartElement("Player"));
        add(factory.createAttribute("id", id));
        add(factory.createAttribute("name", name));
        add(factory.createAttribute("symbol", symbol));
        add(createEndElement("Player"));
        add(createEndElement("GameResult"));
    }

    public void result2xmlDraw() throws XMLStreamException {
        add(end);
        add(tab);

        add(createStartElement("GameResult"));
        add(factory.createCharacters("Draw"));
        add(createEndElement("GameResult"));
    }

    private StartElement createStartElement(String name){
        return factory.createStartElement("", "", name);
    }
    private EndElement createEndElement(String name){
        return factory.createEndElement("", "", name);
    }
    private void add(XMLEvent event) throws XMLStreamException { writer.add(event); }

}
