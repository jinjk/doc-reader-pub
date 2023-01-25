package com.utilsrv.preader.jpa.repository;

import com.utilsrv.preader.jpa.entities.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends CrudRepository<Person, Long> {
    @Query("select p from Person p where p.userId=:userId and p.userIdType.id=:userIdType")
    Person findByUserIdAndType(@Param("userId") String userId, @Param("userIdType") int userIdType);
}
