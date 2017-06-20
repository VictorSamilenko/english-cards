package ua.cards.service;

import org.junit.Before;
import org.junit.Ignore;
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

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, JpaConfig.class})
@ActiveProfiles("test")
@Transactional
public class GroupServiceTest {
    @Autowired
    private GroupService groupService;
    @Autowired
    private UserService userService;
    @Autowired
    private DataSource dataSource;


    private Set<Group> groups;
    private User user1, user2, admin;
    private Iterator<Group> iterator;

    @Before
    public void init() throws SQLException {
        TestDBUtils.clearDB(dataSource);
        user1 = new User("login", "password", "email");
        user1.setRole(User.UserRoles.USER);
        userService.save(user1);

        user2 = new User("login 2", "password 2", "email 2");
        user2.setRole(User.UserRoles.USER);
        userService.save(user2);

        admin = new User("admin login", "admin pass", "admin email");
        admin.setRole(User.UserRoles.ADMIN);
        userService.save(admin);

        groups = new LinkedHashSet<>();
        Group group;
        for (int i = 1; i < 10; i++) {
            group = new Group("name " + i, "description " + i);
            if (i < 5)
                groupService.save(user1, group);
            else if (i != 9)
                groupService.save(user2, group);
            else groupService.save(admin, group);
            groups.add(group);
        }

        iterator = groups.iterator();
    }

    @Test
    @Ignore
    public void testGet() {
        iterator.next();
        Group group = new Group(iterator.next());
        assertThat(groupService.get(2L)).isEqualsToByComparingFields(group);
    }

    @Test
    public void testSaveByUser() {
        Group originalGroup = iterator.next();
        originalGroup.setName("new group name");
        originalGroup.setComment("new group comment");
        groupService.save(user1, originalGroup);

        assertThat(originalGroup.getUser()).isEqualsToByComparingFields(user1);
    }

    @Test
    public void testSaveByAdmin() {
        Group originalGroup = iterator.next();
        originalGroup.setName("new group name");
        originalGroup.setComment("new group comment");
        groupService.save(admin, originalGroup);

        Group group = groupService.get(originalGroup.getId());
        assertThat(group).isEqualsToByComparingFields(originalGroup);
    }

    @Test
    public void testGetAll() {
        Group[] testArray = new Group[groups.size()];
        groups.toArray(testArray);

        Set<Group> set = groupService.getAll();
        assertThat(set).containsOnly(testArray);
    }

    @Test
    public void testDelete() {
        Group group = iterator.next();
        groupService.delete(group.getId());
        Set<Group> set = groupService.getAll();
        assertThat(set).doesNotContain(group);
    }

    @Test
    public void testDeleteByWrongUser() {
        Group group = iterator.next();
        groupService.delete(user2, group.getId());
        Set<Group> set = groupService.getAll();
        assertThat(set).contains(group);
    }

    @Test
    public void testDeleteByRightUser() {
        Group group = iterator.next();
        groupService.delete(user1, group.getId());
        Set<Group> set = groupService.getAll();
        assertThat(set).doesNotContain(group);
    }

    @Test
    public void testGetByUser() {
        Set<Group> set = groupService.get(user2);
        Set<Group> expectedSet = groups.stream().filter((Group gr) -> {
            return gr.getUser() == null || user2.equals(gr.getUser());
        }).collect(Collectors.toSet());

        assertThat(expectedSet).isEqualTo(set);

        set = groupService.get(user1);
        assertThat(expectedSet).isNotEqualTo(set);
    }

    @Test
    public void testGetByAdmin() {
        Set<Group> set = groupService.get(admin);
        assertThat(set).isEqualTo(groups);
    }

    @Test
    public void testGetByNoneUser() {
        Set<Group> set = groupService.get(null);
        Set<Group> expectedSet = groups.stream().filter((Group gr) -> {
            return gr.getUser() == null;
        }).collect(Collectors.toSet());
        assertThat(set).isEqualTo(expectedSet);
    }
}

