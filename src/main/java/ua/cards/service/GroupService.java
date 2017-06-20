package ua.cards.service;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.repository.GroupRepository;

import java.util.Set;

@Service
public class GroupService {

    @Autowired
    GroupRepository repository;

    public Set<Group> getAll() {
        return repository.getAll();
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    private Set<Group> getAllByUser(Long userId) {
        return repository.getAll(
                Restrictions.or(
                        Restrictions.eq("u.id", userId),
                        Restrictions.isNull("g.user")
                )
        );
    }

    private Set<Group> getAllPublic() {
        return repository.getAll(Restrictions.isNull("g.user"));
    }

    public void save(User user, Group group) {
        if (!user.isAdmin()) {
            group.setUser(user);
        }
        repository.save(group);
    }

    public Group get(long id) {
        return repository.get(id);
    }

    public Set<Group> get(User user) {
        if (user != null) {
            if (user.isAdmin())
                return getAll();
            else {
                return getAllByUser(user.getId());
            }
        } else {
            return getAllPublic();
        }
    }

    public boolean delete(User user, long id) {
        Group group = get(id);
        Set<Group> set = user.getGroups();
        if (user.equals(group.getUser()) || set.contains(group)) {
            repository.delete(id);
            return true;
        }
        return false;
    }
}
