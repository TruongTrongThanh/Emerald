package org.emerald.comicapi.repository;

import org.emerald.comicapi.model.data.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Query;

public class ChapterRepositoryImpl implements ChapterRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;
    
    @Override
    public Page<Chapter> findWithComicInfo(Pageable pageable) {
        LookupOperation lookupStage = Aggregation.lookup("comics",
                                                         "comicId",
                                                         "_id", 
                                                         "comic");
        SkipOperation skipStage = Aggregation.skip(pageable.getOffset());
        LimitOperation limitStage = Aggregation.limit(pageable.getPageSize());
        SortOperation sortStage = Aggregation.sort(pageable.getSort());
        UnwindOperation unwindStage = Aggregation.unwind("comic");
        ProjectionOperation projectStage =
            Aggregation.project("id", "name", "createdAt")
                       .and("comic").nested(
                               Aggregation.fields(
                                       "comic._id",
                                       "comic.name",
                                       "comic.thumbUrl"));
        Aggregation aggregation = Aggregation.newAggregation(lookupStage, skipStage, limitStage, sortStage, unwindStage, projectStage);
        AggregationResults<Chapter> results = mongoTemplate.aggregate(aggregation, Chapter.class, Chapter.class);
        long total = mongoTemplate.count(new Query(), Chapter.class);
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }
}