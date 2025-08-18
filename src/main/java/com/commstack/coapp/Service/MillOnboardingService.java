package com.commstack.coapp.Service;

import com.commstack.coapp.Models.MillOnboarding;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface MillOnboardingService {
    ResponseEntity<MillOnboarding> create(MillOnboarding millOnboarding, Principal principal);

    List<MillOnboarding> getAll();

    MillOnboarding getById(String id);

    ResponseEntity<MillOnboarding> update(String id, MillOnboarding millOnboarding, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<MillOnboarding> approve(String id, Principal principal);

    ResponseEntity<MillOnboarding> deactivate(String id, Principal principal);

    ResponseEntity<MillOnboarding> activate(String id, Principal principal);

    ResponseEntity<MillOnboarding> reject(String id, String reason, Principal principal);

    ResponseEntity<MillOnboarding> pushBack(String id, String reason, Principal principal);
}
