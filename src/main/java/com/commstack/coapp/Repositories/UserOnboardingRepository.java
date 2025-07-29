package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.UserOnboarding;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserOnboardingRepository extends MongoRepository<UserOnboarding, String> {
    List<UserOnboarding> findByStatus(String status);
}
