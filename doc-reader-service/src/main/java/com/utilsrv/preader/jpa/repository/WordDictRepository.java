package com.utilsrv.preader.jpa.repository;

import com.utilsrv.preader.jpa.entities.WordDictionary;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface WordDictRepository extends CrudRepository<WordDictionary, Integer> {
    @Cacheable(value="dict")
    Optional<WordDictionary> findByWord(String word);
}
