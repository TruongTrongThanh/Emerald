package org.emerald.comicapi.repository;

import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class ChapterRepositoryImpl implements ChapterRepositoryCustom {

    @Autowired
    MongoTemplate mongoTemplate;
    
    @Override
    public Page<Chapter> findWithComicInfo(Pageable pageable) {
        LookupOperation comicLookupStage = Aggregation.lookup("comics",
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
        Aggregation aggregation = Aggregation.newAggregation(comicLookupStage, skipStage, limitStage, sortStage, unwindStage, projectStage);
        AggregationResults<Chapter> results = mongoTemplate.aggregate(aggregation, Chapter.class, Chapter.class);
        long total = mongoTemplate.count(new Query(), Chapter.class);
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }

    @Override
    public Page<Chapter> findByComicIdWithTotalPages(ObjectId comicId, Pageable pageable) {
        MatchOperation matchStage = Aggregation.match(Criteria.where("comicId").is(comicId));
        LookupOperation lookupStage = Aggregation.lookup("papers",
                                                         "_id",
                                                         "chapterId",
                                                         "papers");
        SkipOperation skipStage = Aggregation.skip(pageable.getOffset());
        LimitOperation limitStage = Aggregation.limit(pageable.getPageSize());
        SortOperation sortStage = Aggregation.sort(pageable.getSort());
        ProjectionOperation projectStage =
                Aggregation.project("id", "name", "errors", "createdAt", "modifiedAt")
                           .and("papers").size().as("totalPages");
        Aggregation aggregation = Aggregation.newAggregation(matchStage, lookupStage, skipStage, limitStage, sortStage, projectStage);
        AggregationResults<Chapter> results = mongoTemplate.aggregate(aggregation, Chapter.class, Chapter.class);
        long total = mongoTemplate.count(new Query(), Chapter.class);
        return new PageImpl<>(results.getMappedResults(), pageable, total);
    }
}