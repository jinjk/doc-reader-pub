package com.utilsrv.preader.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


/**
 * The persistent class for the person database table.
 * 
 */
@Entity
@Table(name = "person")
@Getter
@Setter
public class Person implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "USER_ID", nullable = false, length = 256)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIMARY_ACCOUNT")
    private Person primaryAccount;

    @Column(name = "PWD", length = 512)
    private String pwd;

    @Column(name = "`DEFAULT`")
    private Byte defaultField;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE", nullable = false)
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "primaryAccount", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Person> linkedAccounts = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID_TYPE")
    private UserIdType userIdType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Person person = (Person) o;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}