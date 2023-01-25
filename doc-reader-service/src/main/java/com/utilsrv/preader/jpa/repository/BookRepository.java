package com.utilsrv.preader.jpa.repository;

import com.utilsrv.preader.jpa.entities.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepository extends CrudRepository<Book, Long> {
    Book findBySha1(String sha1);

    @Query("select b from Book b order by b.id desc")
    List<Book> findBooks(Pageable pageable);
}
