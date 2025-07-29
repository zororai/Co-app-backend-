package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.SecurityCompany;
import com.commstack.coapp.Service.SecurityCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/security-companies")
@CrossOrigin(origins = "*")
public class SecurityCompanyController {

    @Autowired
    private SecurityCompanyService service;

    @PostMapping("/register")
    public ResponseEntity<String> registerCompany(@RequestBody SecurityCompany company, Principal principal) {
        return service.create(company, principal);
    }

    @GetMapping
    public List<SecurityCompany> getAllCompanies() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SecurityCompany getCompanyById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/email/{emailAddress}")
    public SecurityCompany getCompanyByEmail(@PathVariable String emailAddress) {
        return service.findByEmailAddress(emailAddress);
    }

    @GetMapping("/bp/{bpNumber}")
    public SecurityCompany getCompanyByBPNumber(@PathVariable String bpNumber) {
        return service.findByBpNumber(bpNumber);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCompany(@PathVariable String id,
            @RequestBody SecurityCompany company, Principal principal) {
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
    public ResponseEntity<String> rejectCompany(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBackCompany(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<String> getPendingCompanies() {
        return service.getAllPendingCompanies();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<String> getApprovedCompanies() {
        return service.getAllApprovedCompanies();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<String> getRejectedCompanies() {
        return service.getAllRejectedCompanies();
    }

    @GetMapping("/status/pushedback")
    public ResponseEntity<String> getPushedBackCompanies() {
        return service.getAllPushedBackCompanies();
    }
}
