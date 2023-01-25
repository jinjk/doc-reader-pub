package com.utilsrv.preader.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * The persistent class for the reading_list database table.
 * 
 */
@Entity
@Table(name="reading_list")
@Getter @Setter
public class ReadingList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	@Column(name="BOOK_ID")
	private BigInteger bookId;

	@Column(name="BOOKMARK")
	private String bookmark;

	@Column(name="CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name="PERSON_ID")
	private BigInteger personId;

	@Column(name="UPDATED_DATE")
	private LocalDateTime updatedDate;

	public ReadingList() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		ReadingList that = (ReadingList) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}