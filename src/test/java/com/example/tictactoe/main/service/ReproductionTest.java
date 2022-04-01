package com.example.tictactoe.main.service;

import com.example.tictactoe.main.mappers.Logger;
import com.example.tictactoe.main.mappers.components.Gameplay;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ReproductionTest {

    @SpyBean
    private Logger logger;

    private final Reproduction re;

    private final ObjectMapper objectMapper;

    @Autowired
    public ReproductionTest(Reproduction re, ObjectMapper objectMapper) {
        this.re = re;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() throws IOException, XMLStreamException {
        doReturn(mapForTest()).when(logger).gameplayRep(any());
    }

    private Gameplay mapForTest() throws JsonProcessingException {
        String json = "{\n" +
                "  \"players\" : [ {\n" +
                "    \"id\" : 1,\n" +
                "    \"name\" : \"Aya\",\n" +
                "    \"symbol\" : \"X\"\n" +
                "  }, {\n" +
                "    \"id\" : 2,\n" +
                "    \"name\" : \"Eve\",\n" +
                "    \"symbol\" : \"O\"\n" +
                "  } ],\n" +
                "  \"game\" : {\n" +
                "    \"steps\" : [ {\n" +
                "      \"num\" : 1,\n" +
                "      \"playerId\" : 1,\n" +
                "      \"actualStep\" : 1\n" +
                "    }, {\n" +
                "      \"num\" : 2,\n" +
                "      \"playerId\" : 2,\n" +
                "      \"actualStep\" : 2\n" +
                "    }, {\n" +
                "      \"num\" : 3,\n" +
                "      \"playerId\" : 1,\n" +
                "      \"actualStep\" : 4\n" +
                "    }, {\n" +
                "      \"num\" : 4,\n" +
                "      \"playerId\" : 2,\n" +
                "      \"actualStep\" : 7\n" +
                "    }, {\n" +
                "      \"num\" : 5,\n" +
                "      \"playerId\" : 1,\n" +
                "      \"actualStep\" : 5\n" +
                "    }, {\n" +
                "      \"num\" : 6,\n" +
                "      \"playerId\" : 2,\n" +
                "      \"actualStep\" : 6\n" +
                "    }, {\n" +
                "      \"num\" : 7,\n" +
                "      \"playerId\" : 1,\n" +
                "      \"actualStep\" : 3\n" +
                "    }, {\n" +
                "      \"num\" : 8,\n" +
                "      \"playerId\" : 2,\n" +
                "      \"actualStep\" : 8\n" +
                "    }, {\n" +
                "      \"num\" : 9,\n" +
                "      \"playerId\" : 1,\n" +
                "      \"actualStep\" : 9\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"gameResult\" : {\n" +
                "    \"Player\" : {\n" +
                "      \"id\" : 1,\n" +
                "      \"name\" : \"https://i.imgur.com/0ZLaTHU.jpg\",\n" +
                "      \"symbol\" : \"X\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        return objectMapper.readValue(json, Gameplay.class);
    }

    @Test
    void initTest() throws XMLStreamException, IOException {
        System.out.println(re.init("2", false));
    }
}