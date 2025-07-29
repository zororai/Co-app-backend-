package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
