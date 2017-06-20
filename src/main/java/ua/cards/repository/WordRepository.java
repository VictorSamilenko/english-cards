package ua.cards.repository;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.cards.model.User;
import ua.cards.model.Word;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
public class WordRepository implements MainRepository<Word> {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Set<Word> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Word.class)
                .addOrder(Order.asc("nativeWord"));
        return new HashSet<Word>(criteria.list());
    }

    @Override
    public Set<Word> getAll(Criterion criterion) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Word.class)
                .addOrder(Order.asc("nativeWord"))
                .add(criterion);
        return new HashSet<Word>(criteria.list());
    }

    public Set<Word> getAll(User user, String language, int limit, int offset, String searchValue, String dir) {
        String strSQL = "select w1.id, w1.native_word, w1.description, w1.comment, w1.language, " +
            "w2.id as tid, w2.native_word as tnative_word, w2.description as tdescription , w2.comment as tcomment, w2.language as tlanguage " +
            "from (select distinct w1.* " +
                  "from words w1 " +
                  "left join word_groups wg on wg.word_id = w1.id " +
                  "left join groups g on g.id = wg.group_id " +
                  "left join word_translate wt on wt.native_word_id = w1.id " +
                  "left join words w2 on w2.id = wt.translate_word_id " +
                  "where w1.language = '" + language + "' " ;

        if (user != null && !user.isAdmin())
            strSQL += " and ( g.user_id is null or g.user_id = " + user.getId() + " ) ";

        if (searchValue != null && !searchValue.isEmpty())
            strSQL += " and (w1.native_word like '%" + searchValue + "%' or " +
                  "w1.description like '%" + searchValue + "%' or " +
                  "w1.comment like '%" + searchValue + "%' or " +
                  "w2.native_word like '%" + searchValue + "%' or " +
                  "w2.description like '%" + searchValue + "%' or " +
                  "w2.comment like '%" + searchValue + "%' )";

        if (dir != null)
            strSQL += " order by native_word " + dir + " ";

        if (limit != 0 || offset != 0)
            strSQL += "limit " + limit + " offset " + offset + " ";

        strSQL += ") w1 left join word_translate wt on wt.native_word_id = w1.id " +
                "left join words w2 on w2.id = wt.translate_word_id " +
                " ORDER BY native_word " + ((dir != null) ? dir : "ASC");
        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(strSQL);
        query.addEntity(Word.class);
        List list = query.list();
        return new LinkedHashSet<Word>(list);
    }
    public long count(User user, String language) {
        return count(user, language, null);
    }

    public long count(User user, String language, String searchValue) {
        Session session = sessionFactory.getCurrentSession();
        String strSQL = "select distinct count(w1.*) " +
                "from words w1 " +
                "left join word_groups wg on wg.word_id = w1.id " +
                "left join groups g on g.id = wg.group_id " +
                "left join word_translate wt on wt.native_word_id = w1.id " +
                "left join words w2 on w2.id = wt.translate_word_id " +
                "where w1.language = '" + language + "' " +
                (user != null && !user.isAdmin() ? " and ( g.user_id is null or g.user_id = " + user.getId() + " ) ": " ") +
                ((searchValue != null && !searchValue.isEmpty()) ?
                        " and (w1.native_word like '%" + searchValue + "%' or " +
                                " w1.description like '%" + searchValue + "%' or " +
                                " w1.comment like '%" + searchValue + "%' or " +
                                " w2.native_word like '%" + searchValue + "%' or " +
                                " w2.description like '%" + searchValue + "%' or " +
                                " w2.comment like '%" + searchValue + "%' ) " : "");

        SQLQuery query = session.createSQLQuery(strSQL);
        BigInteger bigInteger = (BigInteger) query.list().get(0);
        return bigInteger.longValue();
    }

    @Override
    public Long save(Word word) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(word);
        return word.getId();
    }

    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Word word = (Word) session.get(Word.class, id);
        session.delete(word);
    }

    @Override
    public Word get(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Word word = (Word) session.get(Word.class, id);
        return word;
    }
}
