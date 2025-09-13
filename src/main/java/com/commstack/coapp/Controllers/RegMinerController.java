package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Regminer;
import com.commstack.coapp.Service.RegMinerService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/miners")
public class RegMinerController {

    private final RegMinerService service;

    @PostMapping("/createminers")
    public ResponseEntity<Regminer> create(@RequestBody Regminer miner, Principal principal) {
        ResponseEntity<String> response = service.create(miner, principal);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(miner);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Regminer>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Regminer> getById(@PathVariable String id, Principal principal) {
        Regminer miner = service.getById(id);
        return miner != null ? ResponseEntity.ok(miner) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Regminer> update(@PathVariable String id, @RequestBody Regminer miner, Principal principal) {
        ResponseEntity<String> response = service.update(id, miner, principal);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok(miner);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
    }

    @PostMapping("/{id}/set_for_approval")
    public ResponseEntity<String> setApproval(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PostMapping("/{id}/pushback")
    public ResponseEntity<String> pushBack(@PathVariable String id, @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> reject(@PathVariable String id, @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @GetMapping("/getapproved")
    public List<Regminer> getAllApprovedMiners() {
        return service.getallApprovedMiners();
    }

    @GetMapping("/getrejected")
    public List<Regminer> getAllRejectedMiners() {
        return service.getallRejectedMiners();
    }

    @GetMapping("/getpushedback")
    public List<Regminer> getAllPushedBackMiners() {
        return service.getallPushedBackMiners();
    }

    @GetMapping("/getpending")
    public List<Regminer> getAllPendingMiners() {
        return (List<Regminer>) service.getallPendingMiners();
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Regminer>> getAllMiners() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        service.delete(id, principal);
        return ResponseEntity.noContent().build();
    }
}
