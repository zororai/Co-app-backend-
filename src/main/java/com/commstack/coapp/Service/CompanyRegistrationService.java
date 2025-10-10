package com.commstack.coapp.Service;

import com.commstack.coapp.Models.CompanyRegistration;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface CompanyRegistrationService {
    ResponseEntity<String> create(CompanyRegistration company, Principal principal);

    List<CompanyRegistration> getAll();

    CompanyRegistration getById(String id);

    ResponseEntity<String> update(String id, CompanyRegistration company, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<List<CompanyRegistration>> getAllPendingCompanies();

    ResponseEntity<List<CompanyRegistration>> getAllApprovedCompanies();

    ResponseEntity<List<CompanyRegistration>> getAllRejectedCompanies();

    ResponseEntity<List<CompanyRegistration>> getAllPushedBackCompanies();
}
