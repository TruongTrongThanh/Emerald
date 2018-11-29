package org.emerald.comicapi.repository;

import org.emerald.comicapi.model.data.Comic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ComicRepository extends MongoRepository<Comic,String>, ComicRepositoryCustom {
    boolean existsByNameIgnoreCase(String name);
}