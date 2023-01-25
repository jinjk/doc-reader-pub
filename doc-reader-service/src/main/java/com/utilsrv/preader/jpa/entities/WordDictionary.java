package com.utilsrv.preader.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "word_dictionary")
@Getter
@Setter
public class WordDictionary {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "pin_yin")
    private String pinYin;

    @Column(name = "content")
    private String content;

    @Column(name = "remark")
    private String remark;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WordDictionary that = (WordDictionary) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}