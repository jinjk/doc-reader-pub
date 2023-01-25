package com.utilsrv.preader.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * The persistent class for the book database table.
 * 
 */
@ToString
@Entity
@Table(name = "book")
@Getter @Setter
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private Long id;

	@Column(name="CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name="NAME")
	private String name;

	@Column(name="PATH")
	private String path;

	@Column(name="SHA1")
	private String sha1;

	@Column(name = "HAS_HTML")
	private Boolean hasHtml = false;

	@Column(name = "HAS_WORD_SEG")
	private Boolean hasWordSeg = false;

	@Column(name="STATUS")
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name="UPDATED_DATE")
	private LocalDateTime updatedDate;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="OWNER")
	private Person person;

	@Column(name = "PAGE_COUNT")
	private Integer pageCount;

	public void setHasHtml(Boolean hasHtml) {
		this.hasHtml = hasHtml;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		Book book = (Book) o;
		return id != null && Objects.equals(id, book.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}