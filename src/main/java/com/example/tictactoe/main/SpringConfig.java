package com.example.tictactoe.main;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
@ComponentScan("com.example.tictactoe")
public class SpringConfig {

    @Bean
    public XmlMapper mapper(){
        return new XmlMapper();
    }

    @Bean
    public BufferedReader reader(){
        return new BufferedReader(new InputStreamReader(System.in));
    }

    @Bean
    public XMLOutputFactory outputFactory(){
        return XMLOutputFactory.newInstance();
    }

    @Bean
    public XMLEventFactory factory(){
        return XMLEventFactory.newInstance();
    }

    @Bean
    public XMLEventWriter writer(){
        return new XMLEventWriter() {
            @Override
            public void flush() throws XMLStreamException {

            }

            @Override
            public void close() throws XMLStreamException {

            }

            @Override
            public void add(XMLEvent event) throws XMLStreamException {

            }

            @Override
            public void add(XMLEventReader reader) throws XMLStreamException {

            }

            @Override
            public String getPrefix(String uri) throws XMLStreamException {
                return null;
            }

            @Override
            public void setPrefix(String prefix, String uri) throws XMLStreamException {

            }

            @Override
            public void setDefaultNamespace(String uri) throws XMLStreamException {

            }

            @Override
            public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {

            }

            @Override
            public NamespaceContext getNamespaceContext() {
                return null;
            }
        };
    }
}