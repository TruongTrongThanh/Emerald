package org.emerald.comicapi.repository;

import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChapterRepositoryCustom {
    Page<Chapter> findWithComicInfo(Pageable pageable);
    Page<Chapter> findByComicIdWithTotalPages(ObjectId comicId, Pageable pageable);
}