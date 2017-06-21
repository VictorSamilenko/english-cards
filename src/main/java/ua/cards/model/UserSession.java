package ua.cards.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "USER_SESSION")
public class UserSession implements DomainModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip", nullable = false)
    private String ip;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "hashcode", nullable = false)
    private String hashCode;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserSession() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
