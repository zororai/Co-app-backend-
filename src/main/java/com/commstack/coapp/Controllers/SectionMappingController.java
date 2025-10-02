package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.SectionMapping;
import com.commstack.coapp.Service.SectionMappingService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sectionmapping")
@SecurityRequirement(name = "Bearer Authentication")

@Validated
public class SectionMappingController {

    private final SectionMappingService service;

    public SectionMappingController(SectionMappingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SectionMapping> create(@Valid @RequestBody SectionMapping body) {
        SectionMapping created = service.create(body);
        return ResponseEntity.created(URI.create("/api/sectionmapping/" + created.getId())).body(created);
    }

    @GetMapping
    public List<SectionMapping> list() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionMapping> get(@PathVariable String id) {
        SectionMapping s = service.getById(id);
        return s == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(s);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionMapping> update(@PathVariable String id, @Valid @RequestBody SectionMapping body) {
        SectionMapping updated = service.update(id, body);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
