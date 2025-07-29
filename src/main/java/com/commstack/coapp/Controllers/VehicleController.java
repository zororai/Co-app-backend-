package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Vehicle;
import com.commstack.coapp.Service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService service;

    @PostMapping("/register")
    public ResponseEntity<String> registerVehicle(@RequestBody Vehicle vehicle, Principal principal) {
        return service.create(vehicle, principal);
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/reg-number/{regNumber}")
    public Vehicle getVehicleByRegNumber(@PathVariable String regNumber) {
        return service.findByRegNumber(regNumber);
    }

    @GetMapping("/owner-name/{ownerName}")
    public List<Vehicle> getVehiclesByOwnerName(@PathVariable String ownerName) {
        return service.findByOwnerName(ownerName);
    }

    @GetMapping("/owner-id/{ownerIdNumber}")
    public Vehicle getVehicleByOwnerIdNumber(@PathVariable String ownerIdNumber) {
        return service.findByOwnerIdNumber(ownerIdNumber);
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
