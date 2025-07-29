package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Driver;
import com.commstack.coapp.Service.DriverService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @PostMapping("/register")
    public ResponseEntity<String> registerDriver(@RequestBody Driver driver, Principal principal) {
        return service.create(driver, principal);
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/email/{email}")
    public Driver getDriverByEmail(@PathVariable String email) {
        return service.findByEmail(email);
    }

    @GetMapping("/id-number/{idNumber}")
    public Driver getDriverByIdNumber(@PathVariable String idNumber) {
        return service.findByIdNumber(idNumber);
    }

    @GetMapping("/license/{licenseNumber}")
    public Driver getDriverByLicenseNumber(@PathVariable String licenseNumber) {
        return service.findByLicenseNumber(licenseNumber);
    }

    @GetMapping("/company/{companyName}")
    public List<Driver> getDriversByCompany(@PathVariable String companyName) {
        return service.findByCompanyName(companyName);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateDriver(@PathVariable String id,
            @RequestBody Driver driver, Principal principal) {
        return service.update(id, driver, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveDriver(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectDriver(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<String> pushBackDriver(@PathVariable String id,
            @RequestParam String reason, Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<String> getPendingDrivers() {
        return service.getAllPendingDrivers();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<String> getApprovedDrivers() {
        return service.getAllApprovedDrivers();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<String> getRejectedDrivers() {
        return service.getAllRejectedDrivers();
    }

    @GetMapping("/status/pushedback")
    public ResponseEntity<String> getPushedBackDrivers() {
        return service.getAllPushedBackDrivers();
    }
}
