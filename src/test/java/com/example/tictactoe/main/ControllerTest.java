package com.example.tictactoe.main;

import com.example.tictactoe.main.mappers.FileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Path;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @TempDir
    static Path tempDir;

    @MockBean
    private FileRepo fileRepo;

    @BeforeEach
    void setUp(){
        when(fileRepo.getBasePath()).thenReturn(tempDir);
    }

    @Test
    void handleFileUpload() throws Exception {
        MockMultipartFile jsonFile = new MockMultipartFile("file", "ebatb.json", MediaType.APPLICATION_JSON_VALUE, "{\"ктоя\": \"актоты\"}".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gameplay/rep/upload").file(jsonFile)).andExpect(status().isOk());

    }
}