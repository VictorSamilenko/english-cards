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
import ua.cards.config.JpaConfig;
import ua.cards.config.WebConfig;
import ua.cards.model.User;
import ua.cards.model.User.UserRoles;
import ua.cards.model.UserSession;

import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, JpaConfig.class})
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;
    private User user1, user2, admin;
    private final static String USER1_LOGIN = "USER1_LOGIN";
    private final static String USER1_PASSWORD = "USER1_PASSWORD";
    private final static String USER1_HASHCODE = "c82267a700f6ddd9299a90d746f851d8";
    private final static String USER2_LOGIN = "USER2_LOGIN";
    private final static String USER2_PASSWORD = "USER2_PASSWORD";
    private final static String USER2_HASHCODE = "78a00b4b9cb00a60645523e4796b208c";
    private final static String ADMIN_LOGIN = "ADMIN_LOGIN";
    private final static String ADMIN_PASSWORD = "ADMIN_PASSWORD";
    private final static String ADMIN_HASHCODE = "c1c2b383c7dff70a849a15cfb507895a";

    private final static String IP = "IP";
    private final static String USER_AGENT = "USER_AGENT";

    @Before
    public void init() {
        user1 = new User(USER1_LOGIN, USER1_PASSWORD, USER1_HASHCODE);
        user1.setRole(UserRoles.USER);
        userService.save(user1);
        userService.createUserSession(IP, USER_AGENT, user1);

        user2 = new User(USER2_LOGIN, USER2_PASSWORD, USER2_HASHCODE);
        user2.setRole(UserRoles.USER);
        userService.save(user2);
//        userService.createUserSession(IP, USER_AGENT, user2);

        admin = new User(ADMIN_LOGIN, ADMIN_PASSWORD, ADMIN_HASHCODE);
        admin.setRole(UserRoles.ADMIN);
        userService.save(admin);
        userService.createUserSession(IP, USER_AGENT, admin);
    }

    @Test
    public void testGetAll() {
        Set<User> set = userService.getAll();
        assertThat(set).containsOnly(admin, user1, user2);
    }

    @Test
    public void testDelete() {
        userService.delete(user2.getId());
        Set<User> set = userService.getAll();
        assertThat(set).doesNotContain(user2);
    }

    @Test
    public void testGetByID() {
        User user = userService.getUser(admin.getId());
        assertThat(user).isEqualsToByComparingFields(admin);
    }

    @Test
    public void testSave() {
        User expectUser = new User("LOGIN", "PASSWORD", "EMAIL");
        Set<User> set = userService.getAll();
        assertThat(set).doesNotContain(expectUser);
        userService.save(expectUser);
        User user = userService.getUser(expectUser.getId());
        assertThat(expectUser).isEqualsToByComparingFields(user);
    }

    @Test
    public void testGetByLogin() {
        User user = userService.getUser(USER1_LOGIN);
        assertThat(user).isEqualsToByComparingFields(user1);

        user = userService.getUser(USER1_HASHCODE);
        assertThat(user).isNull();
    }

    @Test
    public void testGetByLoginAndPassword() {
        User user = userService.getUser(USER2_LOGIN, USER2_PASSWORD);
        assertThat(user).isEqualsToByComparingFields(user2);

        user = userService.getUser(USER2_LOGIN, USER1_PASSWORD);
        assertThat(user).isNull();

        user = userService.getUser(USER1_LOGIN, USER2_PASSWORD);
        assertThat(user).isNull();

        user = userService.getUser("", USER2_PASSWORD);
        assertThat(user).isNull();

        user = userService.getUser(USER2_LOGIN, "");
        assertThat(user).isNull();
    }

    @Test
    public void testGetByHashCode() {
        User user = userService.getUserByHashCode("");
        assertThat(user).isNull();

        user = userService.getUserByHashCode(ADMIN_LOGIN);
        assertThat(user).isNull();

        user = userService.getUserByHashCode(ADMIN_HASHCODE);
        assertThat(user).isEqualsToByComparingFields(admin);
    }

    @Test
    public void testGetSession() {
        UserSession session = userService.getUserSession(null);
        assertThat(session).isNull();

        session = userService.getUserSession(USER1_LOGIN);
        assertThat(session).isNull();

        session = userService.getUserSession(USER1_HASHCODE);

        UserSession expectSession = new UserSession();
        expectSession.setIp(IP);
        expectSession.setUserAgent(USER_AGENT);
        expectSession.setUser(user1);
        expectSession.setHashCode(USER1_HASHCODE);
        expectSession.setTime(session.getTime());
        expectSession.setId(session.getId());
        assertThat(expectSession).isEqualsToByComparingFields(session);
    }

    @Test
    public void testCreateSession() {
        UserSession session = userService.getUserSession(USER2_HASHCODE);
        assertThat(session).isNull();

        userService.createUserSession(IP, USER_AGENT, user2);
        session = userService.getUserSession(USER2_HASHCODE);
        UserSession expectSession = new UserSession();
        expectSession.setIp(IP);
        expectSession.setUserAgent(USER_AGENT);
        expectSession.setUser(user2);
        expectSession.setHashCode(USER2_HASHCODE);
        expectSession.setTime(session.getTime());
        expectSession.setId(session.getId());
        assertThat(expectSession).isEqualsToByComparingFields(session);

        userService.createUserSession(IP, USER_AGENT, user1);
        session = userService.getUserSession(USER1_HASHCODE);
        expectSession = new UserSession();
        expectSession.setIp(IP);
        expectSession.setUserAgent(USER_AGENT);
        expectSession.setUser(user1);
        expectSession.setHashCode(USER1_HASHCODE);
        expectSession.setTime(session.getTime());
        expectSession.setId(session.getId());
        assertThat(expectSession).isEqualsToByComparingFields(session);
    }

    @Test
    public void testDeleteSession() {
        UserSession session = userService.getUserSession(ADMIN_HASHCODE);
        assertThat(session).isNotNull();

        userService.deleteUserSession(ADMIN_HASHCODE);

        session = userService.getUserSession(ADMIN_HASHCODE);
        assertThat(session).isNull();
    }
}
