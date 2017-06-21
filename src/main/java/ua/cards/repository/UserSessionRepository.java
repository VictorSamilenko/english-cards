package ua.cards.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.cards.model.UserSession;

import java.util.HashSet;
import java.util.Set;

@Repository
public class UserSessionRepository implements MainRepository<UserSession> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Set<UserSession> getAll() {
        return null;
    }

    @Override
    public Set<UserSession> getAll(Criterion criterion) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(UserSession.class);
        criteria.add(criterion);
        return new HashSet<UserSession>(criteria.list());
    }

    @Override
    public Long save(UserSession userSession) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(userSession);
        return userSession.getId();
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        session.delete((UserSession) session.get(UserSession.class, id));
    }

    public void delete(UserSession userSession) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(userSession);
    }

    @Override
    public UserSession get(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return (UserSession) session.get(UserSession.class, id);
    }
}
