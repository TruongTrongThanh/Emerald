package org.emerald.comicapi.repository;

import org.emerald.comicapi.model.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
    Optional<User> findByUsername(String username);
}
