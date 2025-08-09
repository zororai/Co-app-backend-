package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.UserOnboarding;
import com.commstack.coapp.Service.UserOnboardingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserOnboardingController {

    private final UserOnboardingService service;

    @PostMapping("/create")
    public ResponseEntity<UserOnboarding> create(@RequestBody UserOnboarding user, Principal principal) {
        ResponseEntity<String> response = service.create(user, principal);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserOnboarding>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOnboarding> getById(@PathVariable String id) {
        UserOnboarding user = service.getById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserOnboarding> update(@PathVariable String id, @RequestBody UserOnboarding user,
            Principal principal) {
        ResponseEntity<String> response = service.update(id, user, principal);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable String id, @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBack(@PathVariable String id, @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/pending")
    public ResponseEntity<String> getAllPendingUsers() {
        return service.getAllPendingUsers();
    }

    @GetMapping("/approved")
    public ResponseEntity<String> getAllApprovedUsers() {
        return service.getAllApprovedUsers();
    }

    @GetMapping("/rejected")
    public ResponseEntity<String> getAllRejectedUsers() {
        return service.getAllRejectedUsers();
    }

    @GetMapping("/pushedback")
    public ResponseEntity<String> getAllPushedBackUsers() {
        return service.getAllPushedBackUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        ResponseEntity<String> response = service.delete(id, principal);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
    }
}
