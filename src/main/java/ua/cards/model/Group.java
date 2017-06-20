package ua.cards.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "GROUPS")
public class Group implements DomainModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "comment")
    private String comment;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private Set<Word> words = new HashSet<Word>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Group(Long id) {
        this.id = id;
    }

    public Group(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.comment = group.getComment();
        this.user = group.getUser();
        this.words = group.getWords();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Word> getWords() {
        return words;
    }

    public void setWords(Set<Word> words) {
        this.words = words;
    }

    public Group() {
    }

    public Group(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public Group(Long id,String name, String comment) {
        this.id = id;
        this.name = name;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        if (id != null ? !id.equals(group.id) : group.id != null) return false;
        if (name != null ? !name.equals(group.name) : group.name != null) return false;
        return comment != null ? comment.equals(group.comment) : group.comment == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
