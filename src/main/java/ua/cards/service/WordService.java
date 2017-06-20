package ua.cards.service;

import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.model.Word;
import ua.cards.repository.WordRepository;
import ua.cards.util.Utils;

import java.util.Set;

@Service
@Transactional
public class WordService {

    @Autowired
    WordRepository repository;

    public Set<Word> getAll(User user, String language, int limit, int offset, String searchValue, String dir) {
        Set<Word> words = repository.getAll(user, language, limit, offset, searchValue, dir);
        return words;
    }

    public Set<Word> likeByValue(String value) {
        Set<Word> words = repository.getAll(Restrictions.like("nativeWord", value, MatchMode.START));
        return words;
    }

    public Long save(Word word) {
        return repository.save(word);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public Word get(Long id) {
        if (id != null)
            return repository.get(id);
        return null;
    }

    public Word getByNative(String language, String nativeWord) {
        Set<Word> words = repository.getAll(
                Restrictions.and(
                        Restrictions.eq("language", language),
                        Restrictions.eq("nativeWord", nativeWord)
                )
        );
        if (words.size() > 0)
            return words.iterator().next();
        return null;
    }

    public long count(User user, String language) {
        return count(user, language, null);
    }

    public long count(User user, String language, String searchValue) {
        return repository.count(user, language, searchValue);
    }

    public void save(Word word, Group group) {
        repository.save(word);
    }

    public void save(Word word, String[] translations) {
        word.getTranslations().clear();
        for (String s : translations) {
            if ("".equals(s)) {
                continue;
            }
            Word translation = getByNative(Utils.getTranslationLanguage(word.getLanguage()), s);
            if (translation == null) {
                translation = new Word(s);
                translation.setLanguage(Utils.getTranslationLanguage(word.getLanguage()));
                translation.setGroup(word.getGroup());
                save(translation);
            }
            word.getTranslations().add(translation);
        }
        repository.save(word);

        for (Word translation : word.getTranslations()) {
            translation.getTranslations().add(word);
            save(translation);
        }

    }
}
