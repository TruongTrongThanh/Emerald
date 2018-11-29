package org.emerald.comicapi.repository;

import org.emerald.comicapi.model.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.concurrent.atomic.AtomicBoolean;

public class UserRepositoryImpl implements UserRepositoryCustom {
    private final String PREFIX = "ROLE_";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public boolean existsInRoles(String role) {
        Query query = new Query(
                Criteria.where("authorities")
                        .all(new SimpleGrantedAuthority(PREFIX + role)));
        return mongoTemplate.exists(query, User.class);
    }
}
