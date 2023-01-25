package com.utilsrv.preader.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class BookPersonId implements Serializable {
    private static final long serialVersionUID = -5372907295952548013L;
    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BookPersonId that = (BookPersonId) o;
        return bookId != null && Objects.equals(bookId, that.bookId)
                && personId != null && Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, personId);
    }
}