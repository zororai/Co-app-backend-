package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.UserOnboarding;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserOnboardingRepository extends MongoRepository<UserOnboarding, String> {
    List<UserOnboarding> findByStatus(String status);

    Optional<UserOnboarding> findByEmail(String email);
}
