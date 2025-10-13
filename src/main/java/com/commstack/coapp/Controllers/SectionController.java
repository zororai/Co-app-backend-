package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Section;
import com.commstack.coapp.Service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService service;

    @PostMapping("/create")
    public ResponseEntity<String> createSection(@RequestBody Section section, Principal principal) {
        return service.create(section, principal);
    }

    @GetMapping
    public List<Section> getAllSections() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Section getSectionById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSection(@PathVariable String id,
            @RequestBody Section section, Principal principal) {
        return service.update(id, section, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSection(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveSection(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectSection(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBackSection(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<List<Section>> getPendingSections() {
        return service.getAllPendingSections();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<List<Section>> getApprovedSections() {
        return service.getAllApprovedSections();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<List<Section>> getRejectedSections() {
        return service.getAllRejectedSections();
    }

    @GetMapping("/status/pushedback")
    public ResponseEntity<List<Section>> getPushedBackSections() {
        return service.getAllPushedBackSections();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateSection(@PathVariable String id, Principal principal) {
        return service.activateSection(id, principal);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateSection(@PathVariable String id, Principal principal) {
        return service.deactivateSection(id, principal);
    }

    @GetMapping("/status/deactivated-pending")
    @Operation(summary = "Get deactivated pending sections", description = "Retrieves all sections that are deactivated (active=false) and have PENDING status")
    public ResponseEntity<List<Section>> getDeactivatedPendingSections() {
        return service.getDeactivatedPendingSections();
    }
}
