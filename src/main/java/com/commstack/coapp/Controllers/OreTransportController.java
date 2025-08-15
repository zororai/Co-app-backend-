package com.commstack.coapp.Controllers;

import com.commstack.coapp.DTO.OreTransportDTO;
import com.commstack.coapp.Models.OreTransport;
import com.commstack.coapp.Service.OreTransportService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/ore-transports")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class OreTransportController {

    @PutMapping("/{id}/fields")
    public ResponseEntity<String> updateTransportFields(
            @PathVariable String id,
            @RequestParam String selectedTransportdriver,
            @RequestParam String transportStatus,
            @RequestParam String selectedTransport,
            @RequestParam String transportReason,
            Principal principal) {
        return service.updateTransportFields(id, selectedTransportdriver, transportStatus, selectedTransport,
                transportReason, principal);
    }

    private final OreTransportService service;

    @PostMapping("/create")
    public ResponseEntity<OreTransport> create(@RequestBody OreTransportDTO oreTransportDTO, Principal principal) {
        return service.create(oreTransportDTO, principal);
    }

    @GetMapping("/allOre")
    public List<OreTransport> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OreTransport getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody OreTransport oreTransport,
            Principal principal) {
        return service.update(id, oreTransport, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/apply-tax")
    public ResponseEntity<OreTransport> applyTaxAndDeduct(@PathVariable String id, Principal principal) {
        return service.applyTaxAndDeduct(id, principal);
    }
}
