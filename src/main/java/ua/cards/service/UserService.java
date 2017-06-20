package ua.cards.service;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.cards.model.User;
import ua.cards.model.UserSession;
import ua.cards.repository.UserRepository;
import ua.cards.repository.UserSessionRepository;
import ua.cards.util.Utils;

import java.util.Date;
import java.util.Set;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserSessionRepository userSessionRepository;

    public Set<User> getAll() {
        return userRepository.getAll();
    }

    public User getUser(Long id) {
        return userRepository.get(id);
    }

    public User getUser(String login) {
        return userRepository.getUserByLogin(login);
    }

    public User getUserByHashCode(String hashCode) {
        if (hashCode == null || hashCode.isEmpty())
            return null;
        UserSession userSession = getUserSession(hashCode);
        return (userSession != null) ? userSession.getUser() : null;
    }

    public User getUser(String login, String password) {
        User user = userRepository.getUserByLogin(login);
        if (user == null || !user.getPassword().equals(Utils.MD5Encode(password))) {
            user = null;
        }
        return user;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    public String createUserSession(String ip,
                                    String userAgent,
                                    User user) {
        String hashCode = Utils.generateHashCode(userAgent, user);
        UserSession userSession = getUserSession(hashCode);
        if (userSession != null)
            return hashCode;
        userSession = new UserSession();
        userSession.setUser(user);
        userSession.setIp(ip);
        userSession.setTime(new Date());
        userSession.setUserAgent(userAgent);
        userSession.setHashCode(hashCode);
        userSessionRepository.save(userSession);
        return hashCode;
    }

    public UserSession getUserSession(String hashCode) {
        if (hashCode == null || hashCode.isEmpty())
            return null;
        Set<UserSession> userSessions = userSessionRepository.getAll(Restrictions.eq("hashCode", hashCode));
        if (userSessions != null && userSessions.size() > 0)
            return userSessions.iterator().next();
        return null;
    }

    public void deleteUserSession(String hashCode) {
        userSessionRepository.delete(getUserSession(hashCode));
    }


}
