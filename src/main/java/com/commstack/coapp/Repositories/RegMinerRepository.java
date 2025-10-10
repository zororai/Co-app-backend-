package com.commstack.coapp.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.commstack.coapp.Models.Regminer;

public interface RegMinerRepository extends MongoRepository<Regminer, String> {
    long countByStatusIgnoreCase(String status);

}
