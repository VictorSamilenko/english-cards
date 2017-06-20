package ua.cards.repository;


import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import ua.cards.model.DomainModel;

import java.util.Set;

public interface MainRepository<V extends DomainModel>{

    Set<V> getAll();

    Set<V> getAll(Criterion criterion);

    Long save(V v);

    void delete(Long id);

    V get(Long id);
}
