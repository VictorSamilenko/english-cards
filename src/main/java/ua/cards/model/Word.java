package ua.cards.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "words")
public class Word implements DomainModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "native_word")
    private String nativeWord;

    @Column(name = "description")
    private String description;

    @Column(name = "comment", length = 1023)
    private String comment;

    @Column(name = "language", length = 3)
    private String language;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "word_translate", joinColumns = {
            @JoinColumn(name = "native_word_id") },
            inverseJoinColumns = { @JoinColumn(name = "translate_word_id") })
    private Set<Word> translations = new HashSet<Word>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "word_groups", joinColumns = {
            @JoinColumn(name = "word_id") },
            inverseJoinColumns = { @JoinColumn(name = "group_id") })
    private Set<Group> groups = new HashSet<Group>();

//    //TODO: DELETE THIS SET
//    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "translations")
//    private Set<Word> nativesWord = new HashSet<Word>(0);

    public Word(String nativeWord) {
        this.nativeWord = nativeWord;
    }

    public Word(String nativeWord, String language, String description, String comment) {
        this.nativeWord = nativeWord;
        this.language = language;
        this.description = description;
        this.comment = comment;
    }

    public Word(Long id, String nativeWord, String language, String description, String comment) {
        this.id = id;
        this.nativeWord = nativeWord;
        this.language = language;
        this.description = description;
        this.comment = comment;
    }

    public Word() {
    }

    public Word(Long id, String nativeWord) {
        this.id = id;
        this.nativeWord = nativeWord;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNativeWord() {
        return nativeWord;
    }

    public void setNativeWord(String nativeWord) {
        this.nativeWord = nativeWord;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Set<Word> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<Word> translations) {
        this.translations = translations;
    }

//    public Set<Word> getNativesWord() {
//        return nativesWord;
//    }
//
//    public void setNativesWord(Set<Word> nativesWord) {
//        this.nativesWord = nativesWord;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Word)) return false;

        Word word = (Word) o;

        if (id != null ? !id.equals(word.id) : word.id != null) return false;
        if (nativeWord != null ? !nativeWord.equals(word.nativeWord) : word.nativeWord != null) return false;
        if (description != null ? !description.equals(word.description) : word.description != null) return false;
        if (comment != null ? !comment.equals(word.comment) : word.comment != null) return false;
        return language != null ? language.equals(word.language) : word.language == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (nativeWord != null ? nativeWord.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        return result;
    }

    public Group getGroup() {
        if (groups.size() > 0)
            return groups.iterator().next();
        else return null;
    }

    public void setGroup(Group group) {
        if (groups == null)
            groups = new HashSet<>();
        groups.add(group);
    }
}
