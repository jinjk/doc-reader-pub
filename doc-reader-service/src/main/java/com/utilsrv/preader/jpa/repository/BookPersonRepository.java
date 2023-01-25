package com.utilsrv.preader.jpa.repository;

import com.utilsrv.preader.jpa.entities.BookPerson;
import com.utilsrv.preader.jpa.entities.BookPersonId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookPersonRepository extends CrudRepository<BookPerson, BookPersonId> {
    @Query("select bp from BookPerson bp where bp.person.id = :personId")
    List<BookPerson> findByPersonId(@Param("personId") long personId);
}
