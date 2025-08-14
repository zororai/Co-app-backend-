package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Taxdidection;
import com.commstack.coapp.Service.TaxdidectionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/taxdidections")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class TaxdidectionController {
    private final TaxdidectionService service;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Taxdidection taxdidection, Principal principal) {
        return service.create(taxdidection, principal);
    }

    @GetMapping
    public List<Taxdidection> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Taxdidection getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody Taxdidection taxdidection,
            Principal principal) {
        return service.update(id, taxdidection, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approve(@PathVariable String id, Principal principal) {
        return service.approve(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable String id, @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBack(@PathVariable String id, @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }
}
