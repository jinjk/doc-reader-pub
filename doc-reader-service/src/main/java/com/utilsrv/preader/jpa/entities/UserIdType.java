package com.utilsrv.preader.jpa.entities;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;


/**
 * The persistent class for the user_id_type database table.
 * 
 */
@Entity
@Table(name="user_id_type")
@Getter @Setter
public class UserIdType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	private Integer id;

	@Column(name="NAME")
	private String name;


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		UserIdType that = (UserIdType) o;
		return id != null && Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}