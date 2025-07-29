package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserResetRepo extends MongoRepository<UserEntity, Long> {
    UserEntity findByToken(String token);

    UserEntity findByEmail(String email);
}
