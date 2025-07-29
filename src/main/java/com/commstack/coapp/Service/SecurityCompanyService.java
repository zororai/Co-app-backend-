package com.commstack.coapp.Service;

import com.commstack.coapp.Models.SecurityCompany;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface SecurityCompanyService {
    ResponseEntity<String> create(SecurityCompany company, Principal principal);

    List<SecurityCompany> getAll();

    SecurityCompany getById(String id);

    ResponseEntity<String> update(String id, SecurityCompany company, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<String> getAllPendingCompanies();

    ResponseEntity<String> getAllApprovedCompanies();

    ResponseEntity<String> getAllRejectedCompanies();

    ResponseEntity<String> getAllPushedBackCompanies();

    SecurityCompany findByEmailAddress(String emailAddress);

    SecurityCompany findByBpNumber(String bpNumber);
}
