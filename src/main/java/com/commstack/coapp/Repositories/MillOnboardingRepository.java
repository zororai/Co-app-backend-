package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.MillOnboarding;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MillOnboardingRepository extends MongoRepository<MillOnboarding, String> {
}
