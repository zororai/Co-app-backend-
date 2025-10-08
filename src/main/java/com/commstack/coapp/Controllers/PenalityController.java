package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Penality;
import com.commstack.coapp.Service.PenalityService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/penalities")
@SecurityRequirement(name = "Bearer Authentication")
public class PenalityController {

    private final PenalityService penalityService;

    public PenalityController(PenalityService penalityService) {
        this.penalityService = penalityService;
    }

    @PostMapping
    public ResponseEntity<Penality> create(@RequestBody Penality penality, Principal principal) {
        Penality created = penalityService.create(penality, principal);
        return ResponseEntity.created(URI.create("/api/penalities/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Penality> list() {
        return penalityService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Penality> getById(@PathVariable String id) {
        Penality p = penalityService.getById(id);
        return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Penality> update(@PathVariable String id, @RequestBody Penality penality,
            Principal principal) {
        Penality updated = penalityService.update(id, penality, principal);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        penalityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-shaft/{shaftNumber}")
    public List<Penality> findByShaft(@PathVariable String shaftNumber) {
        return penalityService.findByShaftNumber(shaftNumber);
    }

    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<Penality> markAsPaid(@PathVariable String id, Principal principal) {
        Penality p = penalityService.markAsPaid(id, principal);
        return p == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(p);
    }
}
