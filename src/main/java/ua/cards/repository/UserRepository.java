package ua.cards.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.model.UserSession;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class UserRepository implements MainRepository<User>{

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Set<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Set<User> set = new HashSet<User>(session.createCriteria (User.class).list());
        return set;
    }

    @Override
    public Set<User> getAll(Criterion criterion) {
        return null;
    }

    @Override
    public Long save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
        return user.getId();
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = (User) session.get(User.class,id);
        session.delete(user);
    }

    @Override
    public User get(Long id) {
        Session session = sessionFactory.getCurrentSession();
        User user = (User) session.get(User.class,id);
        return user;
    }

    public User getUserByLogin(String login) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("login",login));
        List<User> list = criteria.list();
        return (list!=null&&list.size()>0)?list.get(0):null;
    }
}
