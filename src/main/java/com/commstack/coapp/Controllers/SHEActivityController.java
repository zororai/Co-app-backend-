package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.SHEActivity;
import com.commstack.coapp.Service.SHEActivityService;

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
@RequestMapping("/api/she-activities")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class SHEActivityController {

    private final SHEActivityService service;

    @PostMapping
    public ResponseEntity<String> createActivity(@RequestBody SHEActivity activity, Principal principal) {
        return service.create(activity, principal);
    }

    @GetMapping
    public List<SHEActivity> getAllActivities() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SHEActivity getActivityById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateActivity(
            @PathVariable String id,
            @RequestBody SHEActivity activity,
            Principal principal) {
        return service.update(id, activity, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveActivity(@PathVariable String id, Principal principal) {
        return service.approval(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectActivity(
            @PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/push-back")
    public ResponseEntity<String> pushBackActivity(
            @PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<String> completeActivity(@PathVariable String id, Principal principal) {
        return service.complete(id, principal);
    }

    @GetMapping("/type/{type}")
    public List<SHEActivity> getByActivityType(@PathVariable String type) {
        return service.findByActivityType(type);
    }

    @GetMapping("/date/{date}")
    public List<SHEActivity> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findByDate(date);
    }

    @GetMapping("/date-range")
    public List<SHEActivity> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.findByDateRange(startDate, endDate);
    }

    @GetMapping("/location/{location}")
    public List<SHEActivity> getByLocation(@PathVariable String location) {
        return service.findByLocation(location);
    }

    @GetMapping("/conducted-by/{conductor}")
    public List<SHEActivity> getByConductor(@PathVariable String conductor) {
        return service.findByConductedBy(conductor);
    }

    @GetMapping("/department/{department}")
    public List<SHEActivity> getByDepartment(@PathVariable String department) {
        return service.findByDepartment(department);
    }

    @GetMapping("/supervisor/{supervisor}")
    public List<SHEActivity> getBySupervisor(@PathVariable String supervisor) {
        return service.findBySupervisor(supervisor);
    }

    @GetMapping("/risk-level/{level}")
    public List<SHEActivity> getByRiskLevel(@PathVariable String level) {
        return service.findByRiskLevel(level);
    }

    @GetMapping("/follow-up/required")
    public List<SHEActivity> getActivitiesRequiringFollowUp() {
        return service.findActivitiesRequiringFollowUp();
    }

    @GetMapping("/follow-up/overdue")
    public List<SHEActivity> getOverdueFollowUps(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findOverdueFollowUps(date);
    }

    @GetMapping("/actions/overdue")
    public List<SHEActivity> getOverdueActions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.findOverdueActions(date);
    }

    @GetMapping("/metrics/compliance")
    public ResponseEntity<String> getComplianceMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getComplianceMetrics(startDate, endDate);
    }

    @GetMapping("/metrics/safety")
    public ResponseEntity<String> getSafetyMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getSafetyMetrics(startDate, endDate);
    }

    @GetMapping("/metrics/participation")
    public ResponseEntity<String> getParticipationMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getParticipationMetrics(startDate, endDate);
    }

    @GetMapping("/status/pending")
    public ResponseEntity<String> getPendingActivities() {
        return service.getAllPendingActivities();
    }

    @GetMapping("/status/approved")
    public ResponseEntity<String> getApprovedActivities() {
        return service.getAllApprovedActivities();
    }

    @GetMapping("/status/rejected")
    public ResponseEntity<String> getRejectedActivities() {
        return service.getAllRejectedActivities();
    }

    @GetMapping("/status/pushed-back")
    public ResponseEntity<String> getPushedBackActivities() {
        return service.getAllPushedBackActivities();
    }

    @GetMapping("/status/completed")
    public ResponseEntity<String> getCompletedActivities() {
        return service.getAllCompletedActivities();
    }
}
