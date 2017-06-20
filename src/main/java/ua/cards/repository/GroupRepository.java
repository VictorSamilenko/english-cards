package ua.cards.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.cards.model.Group;

import java.util.HashSet;
import java.util.Set;

@Repository
public class GroupRepository implements MainRepository<Group> {
    @Autowired
    SessionFactory sessionFactory;

    @Override
    public Set<Group> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Set<Group> set = new HashSet<Group>(session.createCriteria (Group.class).list());
        return set;
    }

    @Override
    public Set<Group> getAll(Criterion criterion) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Group.class,"g")
                .createAlias("user","u", JoinType.LEFT_OUTER_JOIN)
                .add(criterion);
        Set<Group> set = new HashSet<Group>(criteria.list());
        return set;
    }

    @Override
    public Long save(Group group) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(group);
        return group.getId();
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Group group = (Group) session.get(Group.class,id);
        session.delete(group);
    }

    @Override
    public Group get(Long id) {
        if (id == null)
            return null;
        Session session = sessionFactory.getCurrentSession();
        Group group = (Group) session.get(Group.class,id);
        return group;
    }
}
