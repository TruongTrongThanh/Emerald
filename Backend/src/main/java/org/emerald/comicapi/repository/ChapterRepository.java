package org.emerald.comicapi.repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChapterRepository extends MongoRepository<Chapter,String>, ChapterRepositoryCustom {
    Long deleteChapterByComicId(ObjectId comicId);
    List<Chapter> findByComicId(ObjectId comicId);
    Page<Chapter> findByComicId(ObjectId comicId, Pageable pageable);
    boolean existsByNameAndComicIdAllIgnoreCase(String name, ObjectId comicId);
}