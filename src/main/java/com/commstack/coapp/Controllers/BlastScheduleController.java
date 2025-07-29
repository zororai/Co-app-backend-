package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.BlastSchedule;
import com.commstack.coapp.Service.BlastScheduleService;

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
@RequestMapping("/api/blast-schedule")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class BlastScheduleController {

    private final BlastScheduleService service;

    @PostMapping
    public ResponseEntity<String> createSchedule(@RequestBody BlastSchedule schedule, Principal principal) {
        return service.create(schedule, principal);
    }

    @GetMapping
    public List<BlastSchedule> getAllSchedules() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BlastSchedule getScheduleById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/blast-zone/{zone}")
    public List<BlastSchedule> getByBlastZone(@PathVariable String zone) {
        return service.findByBlastZone(zone);
    }

    @GetMapping("/date/{date}")
    public List<BlastSchedule> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findByDate(date);
    }

    @GetMapping("/operator/{operator}")
    public List<BlastSchedule> getByOperator(@PathVariable String operator) {
        return service.findByAssignedOperator(operator);
    }

    @GetMapping("/safety-officer/{officer}")
    public List<BlastSchedule> getBySafetyOfficer(@PathVariable String officer) {
        return service.findBySafetyOfficer(officer);
    }

    @GetMapping("/date-range")
    public List<BlastSchedule> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.findByDateRange(startDate, endDate);
    }

    @GetMapping("/blast-type/{type}")
    public List<BlastSchedule> getByBlastType(@PathVariable String type) {
        return service.findByBlastType(type);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSchedule(
            @PathVariable String id,
            @RequestBody BlastSchedule schedule,
            Principal principal) {
        return service.update(id, schedule, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveSchedule(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectSchedule(
            @PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PostMapping("/{id}/push-back")
    public ResponseEntity<String> pushBackSchedule(
            @PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<String> getPendingSchedules() {
        return service.getAllPendingSchedules();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<String> getApprovedSchedules() {
        return service.getAllApprovedSchedules();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<String> getRejectedSchedules() {
        return service.getAllRejectedSchedules();
    }

    @GetMapping("/status/pushed-back")
    public ResponseEntity<String> getPushedBackSchedules() {
        return service.getAllPushedBackSchedules();
    }
}
