package ua.cards.model;

import ua.cards.util.Utils;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class User implements DomainModel {
    public enum UserRoles {
        ADMIN("admin"), USER("user");
        private String role;

        UserRoles(String role) {
            this.role = role;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private UserRoles role;

    @OneToMany(mappedBy = "user")
    private Set<UserSession> userSessions = new HashSet<UserSession>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Group> groups = new HashSet<Group>();

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public User() {
    }

    public User(String login, String password, String email) {
        this.login = login;
        this.password = Utils.MD5Encode(password);
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Utils.MD5Encode(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<UserSession> getSessions() {
        return userSessions;
    }

    public void setSessions(Set<UserSession> userSessions) {
        this.userSessions = userSessions;
    }

    public UserRoles getRole() {
        return role;
    }

    public void setRole(UserRoles role) {
        this.role = role;
    }

    public boolean isAdmin() {
        if (role == UserRoles.ADMIN)
            return true;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!login.equals(user.login)) return false;
        if (!password.equals(user.password)) return false;
        return role.equals(user.role);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + login.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
