package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.TransportCostonboarding;
import com.commstack.coapp.Service.TransportCostonboardingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transport-cost-onboarding")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TransportCostonboardingController {
    private final TransportCostonboardingService service;

    @PostMapping
    public ResponseEntity<TransportCostonboarding> create(@RequestBody TransportCostonboarding entity,
            Principal principal) {
        return ResponseEntity.ok(service.create(entity, principal));
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<TransportCostonboarding> pushBack(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        TransportCostonboarding updated = service.pushBack(id, reason, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<TransportCostonboarding> approve(@PathVariable String id, Principal principal) {
        TransportCostonboarding updated = service.approve(id, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<TransportCostonboarding> reject(@PathVariable String id, Principal principal) {
        TransportCostonboarding updated = service.reject(id, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<TransportCostonboarding>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportCostonboarding> getById(@PathVariable String id) {
        TransportCostonboarding entity = service.getById(id);
        return entity != null ? ResponseEntity.ok(entity) : ResponseEntity.notFound().build();
    }
}
