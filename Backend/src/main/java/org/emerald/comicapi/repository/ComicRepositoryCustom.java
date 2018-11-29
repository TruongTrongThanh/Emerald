package org.emerald.comicapi.repository;

import org.emerald.comicapi.model.data.Comic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ComicRepositoryCustom {
    Page<Comic> findByBasicInfo(Comic probe, Pageable pageable);
    Page<Comic> findByTextIndex(String term, Pageable pageable);
}