package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.OreTransport;
import com.commstack.coapp.Service.OreTransportService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/ore-transports")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")
public class OreTransportController {

    private final OreTransportService service;

    public OreTransportController(OreTransportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> create(@RequestBody OreTransport oreTransport, Principal principal) {
        return service.create(oreTransport, principal);
    }

    @GetMapping
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
}
