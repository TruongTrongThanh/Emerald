package org.emerald.comicapi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class PersistentTokenRepositoryImpl implements PersistentTokenRepository {

    private final String COLLECTION_NAME = "rememberMeTokens";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        mongoTemplate.insert(token, COLLECTION_NAME);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        Query query = new Query(Criteria.where("series").is(series));
        Update update = new Update()
                .set("tokenValue", tokenValue)
                .set("date", lastUsed);
        mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        Query query = new Query(Criteria.where("series").is(seriesId));
        return mongoTemplate.findOne(query, PersistentRememberMeToken.class, COLLECTION_NAME);
    }

    @Override
    public void removeUserTokens(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        mongoTemplate.remove(query, COLLECTION_NAME);
    }
}
