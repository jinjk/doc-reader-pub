package com.utilsrv.preader.jpa.repository;

import com.utilsrv.preader.jpa.entities.UserIdType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserIdTypeRepository extends CrudRepository<UserIdType, Long> {
    @Cacheable("userIdType")
    Optional<UserIdType> findById(Integer id);
}
