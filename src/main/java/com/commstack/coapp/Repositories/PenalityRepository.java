package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Penality;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PenalityRepository extends MongoRepository<Penality, String> {
    List<Penality> findByShaftNumber(String shaftNumber);
}
