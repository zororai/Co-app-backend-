package com.commstack.coapp.Repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.commstack.coapp.Models.Regminer;

public interface RegMinerRepository extends MongoRepository<Regminer, String> {
    Optional<Regminer> findById(String id);
}
