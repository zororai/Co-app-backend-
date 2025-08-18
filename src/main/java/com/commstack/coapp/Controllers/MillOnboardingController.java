package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.MillOnboarding;
import com.commstack.coapp.Service.MillOnboardingService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/mill-onboarding")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")

public class MillOnboardingController {

    @Autowired
    private MillOnboardingService service;

    @GetMapping("/activated")
    public List<MillOnboarding> getAllActivated() {
        return service.getAll().stream().filter(MillOnboarding::isActiveStatus).toList();
    }

    @GetMapping("/deactivated")
    public List<MillOnboarding> getAllDeactivated() {
        return service.getAll().stream().filter(m -> !m.isActiveStatus()).toList();
    }

    @PostMapping("/create")
    public ResponseEntity<MillOnboarding> create(@RequestBody MillOnboarding millOnboarding, Principal principal) {
        return service.create(millOnboarding, principal);
    }

    @GetMapping("/all")
    public List<MillOnboarding> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MillOnboarding getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MillOnboarding> update(@PathVariable String id, @RequestBody MillOnboarding millOnboarding,
            Principal principal) {
        return service.update(id, millOnboarding, principal);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<MillOnboarding> deactivate(@PathVariable String id, Principal principal) {
        return service.deactivate(id, principal);
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<MillOnboarding> activate(@PathVariable String id, Principal principal) {
        return service.activate(id, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<MillOnboarding> approve(@PathVariable String id, Principal principal) {
        return service.approve(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<MillOnboarding> reject(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/push-back")
    public ResponseEntity<MillOnboarding> pushBack(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        return service.pushBack(id, reason, principal);
    }
}
