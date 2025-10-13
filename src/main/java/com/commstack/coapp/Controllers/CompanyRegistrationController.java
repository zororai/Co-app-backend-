package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.CompanyRegistration;
import com.commstack.coapp.Service.CompanyRegistrationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/companies")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class CompanyRegistrationController {

    private final CompanyRegistrationService service;

    @PostMapping("/register")
    public ResponseEntity<String> registerCompany(@RequestBody CompanyRegistration company, Principal principal) {
        return service.create(company, principal);
    }

    @GetMapping
    public List<CompanyRegistration> getAllCompanies() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CompanyRegistration getCompanyById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCompany(@PathVariable String id, @RequestBody CompanyRegistration company,
            Principal principal) {
        return service.update(id, company, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveCompany(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectCompany(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBackCompany(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<List<CompanyRegistration>> getPendingCompanies() {
        return service.getAllPendingCompanies();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<List<CompanyRegistration>> getApprovedCompanies() {
        return service.getAllApprovedCompanies();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<List<CompanyRegistration>> getRejectedCompanies() {
        return service.getAllRejectedCompanies();
    }

    @GetMapping("/status/pushedback")
    public ResponseEntity<List<CompanyRegistration>> getPushedBackCompanies() {
        return service.getAllPushedBackCompanies();
    }
}
