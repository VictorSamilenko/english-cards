package ua.cards.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import ua.cards.TestDBUtils;
import ua.cards.config.JpaConfig;
import ua.cards.config.WebConfig;
import ua.cards.model.Word;
import ua.cards.service.WordService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, JpaConfig.class})
@ActiveProfiles("test")
public class WordControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private WordService wordService;

    @Autowired
    DataSource dataSource;

    private MockMvc mockMvc;
    private List<Word> rusWords;
    private Word engWord1, engWord2;

    private void initWords() {
        rusWords = new ArrayList<>(4);
        engWord1 = new Word("nativeWord 0", "eng", "description 0", "comment 0");
        engWord2 = new Word("nativeWord 1", "eng", "description 1", "comment 1");
        wordService.save(engWord1);
        wordService.save(engWord2);

        Word word;
        word = new Word("nativeWord 1", "rus", "description 1", "comment 81");
        rusWords.add(word);
        engWord1.getTranslations().add(word);
        word.getTranslations().add(engWord1);
        wordService.save(word);

        word = new Word("nativeWord 2", "rus", "description 2", "comment 2");
        rusWords.add(word);
        engWord1.getTranslations().add(word);
        word.getTranslations().add(engWord1);
        wordService.save(word);

        word = new Word("nativeWord 3", "rus", "description 3", "comment 3");
        rusWords.add(word);
        engWord2.getTranslations().add(word);
        word.getTranslations().add(engWord2);
        wordService.save(word);

        word = new Word("nativeWord 4", "rus", "description 4", "comment 4");
        rusWords.add(word);
        engWord2.getTranslations().add(word);
        word.getTranslations().add(engWord2);
        wordService.save(word);
    }

    @Before
    public void setup() throws SQLException {
        this.mockMvc = webAppContextSetup(this.wac).build();
        TestDBUtils.clearDB(dataSource);
        initWords();
    }

    @Test
    public void testSaveWord() throws Exception {
//        this.mockMvc.perform(post("/words")
//                .accept("application/json")
//                .param("native_word","jump")
//                .param("language","eng")
//                .param("description","push oneself off a surface and into the air by using the muscles in one's legs and feet.")
//                .param("comment","the cat jumped off his lap")
//                .param("translations","прыгать, прыжок, скачек"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("action").value("insert"));
    }

    @Test
    public void testGetViewWord() throws Exception {
        mockMvc.perform(get("/words")).andExpect(status().isOk());
    }

    @Test
    public void testGetWord() throws Exception {
        this.mockMvc.perform(get("/words/get")
                .accept("application/json")
                .param("start", "0")
                .param("length", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("iTotalRecords").value(4))
                .andExpect(jsonPath("words").isArray());
    }

    @Test
    public void testAutocomplete() throws Exception {
        this.mockMvc.perform(get("/words/autocomplete")
                .accept("application/json")
                .param("criteria", "nati"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(6)));


        this.mockMvc.perform(get("/words/autocomplete")
                .accept("application/json")
                .param("criteria", "nativeWord 3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)));

        this.mockMvc.perform(get("/words/autocomplete")
                .accept("application/json")
                .param("criteria", "nativeWord 88"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
