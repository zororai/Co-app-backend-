package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Vehicle;
import com.commstack.coapp.Service.VehicleService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "https://coappapi.commapp.online/", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class VehicleController {

    @PutMapping("/{id}/in-transit")
    public ResponseEntity<String> setVehicleToInTransit(@PathVariable String id, Principal principal) {
        return service.setToInTransit(id, principal);
    }

    @PutMapping("/{id}/loading")
    public ResponseEntity<String> setVehicleToLoading(@PathVariable String id, Principal principal) {
        return service.setToLoading(id, principal);
    }

    @PutMapping("/{id}/idle")
    public ResponseEntity<String> setVehicleToIdle(@PathVariable String id, Principal principal) {
        return service.setToIdle(id, principal);
    }

    @PutMapping("/{id}/maintainance")
    public ResponseEntity setVehicleToMaintainance(@PathVariable String id, Principal principal) {
        return service.setToMaintainance(id, principal);
    }

    private final VehicleService service;

    @PostMapping("/register")
    public ResponseEntity<String> registerVehicle(@RequestBody Vehicle vehicle, Principal principal) {
        return service.create(vehicle, principal);
    }

    @GetMapping("/idle")
    public ResponseEntity<List<Vehicle>> getAllIdleVehicles() {
        return ResponseEntity.ok(service.getAllIdleVehicles());
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateVehicle(@PathVariable String id,
            @RequestBody Vehicle vehicle, Principal principal) {
        return service.update(id, vehicle, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveVehicle(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectVehicle(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBackVehicle(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<String> getPendingVehicles() {
        return service.getAllPendingVehicles();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<String> getApprovedVehicles() {
        return service.getAllApprovedVehicles();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<String> getRejectedVehicles() {
        return service.getAllRejectedVehicles();
    }

    @GetMapping("/status/pushedback")
    public ResponseEntity<String> getPushedBackVehicles() {
        return service.getAllPushedBackVehicles();
    }
}
