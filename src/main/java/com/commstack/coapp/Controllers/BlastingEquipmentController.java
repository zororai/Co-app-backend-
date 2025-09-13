package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.BlastingEquipment;
import com.commstack.coapp.Service.BlastingEquipmentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/blasting-equipment")

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class BlastingEquipmentController {

    private final BlastingEquipmentService service;

    @PostMapping
    public ResponseEntity<String> createEquipment(@RequestBody BlastingEquipment equipment, Principal principal) {
        return service.create(equipment, principal);
    }

    @GetMapping
    public List<BlastingEquipment> getAllEquipment() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BlastingEquipment getEquipmentById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEquipment(
            @PathVariable String id,
            @RequestBody BlastingEquipment equipment,
            Principal principal) {
        return service.update(id, equipment, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEquipment(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveEquipment(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectEquipment(
            @PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PostMapping("/{id}/push-back")
    public ResponseEntity<String> pushBackEquipment(
            @PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/type/{type}")
    public List<BlastingEquipment> getByEquipmentType(@PathVariable String type) {
        return service.findByEquipmentType(type);
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public List<BlastingEquipment> getByManufacturer(@PathVariable String manufacturer) {
        return service.findByManufacturer(manufacturer);
    }

    @GetMapping("/assigned-to/{assignee}")
    public List<BlastingEquipment> getByAssignedTo(@PathVariable String assignee) {
        return service.findByAssignedTo(assignee);
    }

    @GetMapping("/location/{location}")
    public List<BlastingEquipment> getByLocation(@PathVariable String location) {
        return service.findByLocation(location);
    }

    @GetMapping("/maintenance-status/{status}")
    public List<BlastingEquipment> getByMaintenanceStatus(@PathVariable String status) {
        return service.findByMaintenanceStatus(status);
    }

    @GetMapping("/operational-status/{status}")
    public List<BlastingEquipment> getByOperationalStatus(@PathVariable String status) {
        return service.findByOperationalStatus(status);
    }

    @GetMapping("/maintenance-due")
    public List<BlastingEquipment> getMaintenanceDue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findMaintenanceDue(date);
    }

    @GetMapping("/calibration-due")
    public List<BlastingEquipment> getCalibrationDue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findCalibrationDue(date);
    }

    @GetMapping("/certification-expiring")
    public List<BlastingEquipment> getCertificationExpiring(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findCertificationExpiring(date);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<String> getPendingEquipment() {
        return service.getAllPendingEquipment();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<String> getApprovedEquipment() {
        return service.getAllApprovedEquipment();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<String> getRejectedEquipment() {
        return service.getAllRejectedEquipment();
    }

    @GetMapping("/status/pushed-back")
    public ResponseEntity<String> getPushedBackEquipment() {
        return service.getAllPushedBackEquipment();
    }
}
