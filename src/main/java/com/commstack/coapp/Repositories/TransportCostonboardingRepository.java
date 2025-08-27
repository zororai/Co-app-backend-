package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.TransportCostonboarding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportCostonboardingRepository extends MongoRepository<TransportCostonboarding, String> {
}
