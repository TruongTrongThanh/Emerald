package org.emerald.comicapi.repository;

import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PaperRepository extends MongoRepository<Paper,String> {
    Long deletePaperByChapterId(ObjectId chapterId);
    @Query(fields = "{ chapterId: 0 }")
    Page<Paper> findByChapterId(ObjectId chapterId, Pageable pageable);
    List<Paper> findByChapterId(ObjectId chapterId);
}