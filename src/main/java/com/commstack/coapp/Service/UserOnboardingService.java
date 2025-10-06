package com.commstack.coapp.Service;

import com.commstack.coapp.Models.UserOnboarding;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface UserOnboardingService {
    ResponseEntity<String> create(UserOnboarding user, Principal principal);

    List<UserOnboarding> getAll();

    UserOnboarding getById(String id);

    ResponseEntity<String> update(String id, UserOnboarding user, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<String> getAllPendingUsers();

    ResponseEntity<String> getAllApprovedUsers();

    ResponseEntity<String> getAllRejectedUsers();

    ResponseEntity<String> getAllPushedBackUsers();

    ResponseEntity<String> getEmailResponse(String email);

    ResponseEntity<UserOnboarding> getByEmail(String email);
}
