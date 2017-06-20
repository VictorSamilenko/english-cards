package ua.cards.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ua.cards.TestDBUtils;
import ua.cards.config.JpaConfig;
import ua.cards.config.WebConfig;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.model.Word;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, JpaConfig.class})
@ActiveProfiles("test")
@Transactional
public class WordServiceTest {

    @Autowired
    WordService wordService;

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    DataSource dataSource;

    private User user1, user2, admin;
    private List<Word> rusWords;
    private Word engWord1, engWord2;

    private void initUsers() {
        user1 = new User("login", "password", "email");
        user1.setRole(User.UserRoles.USER);
        userService.save(user1);

        user2 = new User("login 2", "password 2", "email 2");
        user2.setRole(User.UserRoles.USER);
        userService.save(user2);

        admin = new User("admin login", "admin pass", "admin email");
        admin.setRole(User.UserRoles.ADMIN);
        userService.save(admin);
    }

    private void initWords() {
        rusWords = new ArrayList<>(4);
        engWord1 = new Word("nativeWord 0", "eng", "description 0", "comment 80");
        engWord2 = new Word("nativeWord 1", "eng", "description 1", "comment 1");
        wordService.save(engWord1);
        wordService.save(engWord2);

        Word word;
        word = new Word("nativeWord 11", "rus", "description 21", "comment 81");
        rusWords.add(word);
        engWord1.getTranslations().add(word);
        word.getTranslations().add(engWord1);
        wordService.save(word);

        word = new Word("nativeWord 12", "rus", "description 22", "comment 2");
        rusWords.add(word);
        engWord1.getTranslations().add(word);
        word.getTranslations().add(engWord1);
        wordService.save(word);

        word = new Word("nativeWord 123", "rus", "description 23", "comment 3");
        rusWords.add(word);
        engWord2.getTranslations().add(word);
        word.getTranslations().add(engWord2);
        wordService.save(word);

        word = new Word("nativeWord 14", "rus", "description 4", "comment 84");
        rusWords.add(word);
        engWord2.getTranslations().add(word);
        word.getTranslations().add(engWord2);
        wordService.save(word);
    }

    private void initUserGroups() {
        Group firstUserGroup = new Group("Group 1", "Group words of the first user");
        firstUserGroup.getWords().add(rusWords.get(0));
        firstUserGroup.getWords().add(rusWords.get(1));
        groupService.save(user1, firstUserGroup);

        Group secondUserGroup = new Group("Group 2", "Group words of the second user");
        secondUserGroup.getWords().add(rusWords.get(3));
        groupService.save(user2, secondUserGroup);
    }

    @Before
    public void init() throws SQLException {
        TestDBUtils.clearDB(dataSource);
        initUsers();
        initWords();
        initUserGroups();
    }

    @Test
    public void testGetAllByUser() {
        Set<Word> set = wordService.getAll(user1, "rus", 10, 0, null, null);
        assertThat(set).hasSize(4);
    }

    @Test
    public void testGetByID() {
        Word word = wordService.get(null);
        assertThat(word).isNull();
        word = wordService.get(rusWords.get(0).getId());
        assertThat(word).isEqualsToByComparingFields(rusWords.get(0));
    }

    @Test
    public void testDelete() {
        Set<Word> set = wordService.getAll(admin, "rus", 10, 0, null, null);
        assertThat(set).contains(rusWords.get(0));

        wordService.delete(rusWords.get(0).getId());
        set = wordService.getAll(admin, "rus", 10, 0, null, null);
        assertThat(set).doesNotContain(rusWords.get(0));
    }

    @Test
    public void testCount() {
        long count = wordService.count(admin, "rus");
        assertThat(count).isEqualTo(rusWords.size());

        count = wordService.count(admin, "eng");
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testCountBySearchValue() {
        long count = wordService.count(admin, "rus", "");
        assertThat(count).isEqualTo(4);

        count = wordService.count(admin, "rus", "1");
        assertThat(count).isEqualTo(4);

        count = wordService.count(admin, "rus", "2");
        assertThat(count).isEqualTo(3);

        count = wordService.count(admin, "rus", "8");
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testSaveWithGroup() {
        Word expectWord = new Word("new Word", "rus", "description", "comment");
        Group group = new Group("New Group", "new Comment");
        wordService.save(expectWord, group);
        expectWord.setGroup(group);

        Word word = wordService.getByNative("rus", "new Word");
        expectWord.setId(word.getId());

        assertThat(expectWord).isEqualTo(word);
        assertThat(expectWord.getGroups()).containsAll(word.getGroups());
    }


    @Test
    public void testLikeByValue() {
        Set<Word> set = wordService.likeByValue("native");
        assertThat(set).containsOnly(rusWords.get(0), rusWords.get(1), rusWords.get(2), rusWords.get(3), engWord1, engWord2);

        set = wordService.likeByValue("nativeWord 12");
        assertThat(set).containsOnly(rusWords.get(1), rusWords.get(2));

        set = wordService.likeByValue("non");
        assertThat(set).isEmpty();
    }

    @Test
    public void testSaveWithTranslation() {
        Word expectWord = new Word("new Word", "rus", "description", "comment");
        String[] translations = new String[]{"translation1","translation2","translation3"};
        wordService.save(expectWord, translations);

        Set<Word> set = wordService.getByNative("rus", "new Word").getTranslations();
        assertThat(set).hasSize(3);
    }
}
