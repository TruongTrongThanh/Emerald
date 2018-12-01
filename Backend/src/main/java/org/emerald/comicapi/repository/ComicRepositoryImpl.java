package org.emerald.comicapi.repository;

import org.emerald.comicapi.model.data.Comic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;

import java.util.ArrayList;
import java.util.List;

public class ComicRepositoryImpl implements ComicRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<Comic> findByBasicInfo(Comic probe, Pageable pageable) {
        Query query = new Query();

        query.fields().include("name")
                      .include("author")
                      .include("genres")
                      .include("demographic")
                      .include("coverUrl")
                      .include("thumbUrl")
                      .include("createdAt")
                      .include("modifiedAt");
        
        Criteria criteria = new Criteria();
        List<Criteria> list = new ArrayList<>();

        if (probe.getName() != null)
            list.add(Criteria.where("name").regex(probe.getName(), "i"));
        if (probe.getAuthor() != null)
            list.add(Criteria.where("author").regex(probe.getAuthor(), "i"));
        if (probe.getDemographic() != null)
            list.add(Criteria.where("demographic").regex(probe.getDemographic(), "i"));
        if (probe.getGenres() != null)
            list.add(Criteria.where("genres").all(probe.getGenres()));
        if (probe.getDescription() != null)
            list.add(Criteria.where("description").regex(probe.getDescription(), "i"));

        if (list.size() != 0) {
            criteria.andOperator(list.toArray(new Criteria[list.size()]));
            query.addCriteria(criteria);
        }
        long total = mongoTemplate.count(query, Comic.class);
        List<Comic> comicList = mongoTemplate.find(query.with(pageable), Comic.class);

        return new PageImpl<>(comicList, pageable, total);
    }

    @Override
    public Page<Comic> findByTextIndex(String term, Pageable pageable) {
        TextQuery query = new TextQuery(TextCriteria.forDefaultLanguage()
                                                    .matching(term)
                                                    .caseSensitive(false));

        long total = mongoTemplate.count(query, Comic.class);
        List<Comic> list = mongoTemplate.find(query.with(pageable), Comic.class);
        
        return new PageImpl<>(list, pageable, total);
    }
}