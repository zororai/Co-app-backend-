
package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.ShaftAssignment;
import com.commstack.coapp.Service.ShaftAssignmentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/shaft-assignments")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ShaftAssignmentController {

    @PutMapping("/{id}/update-loan-details")
    public ResponseEntity<ShaftAssignment> updateLoanDetails(
            @PathVariable("id") String shaftAssignmentId,
            @RequestParam String loanName,
            @RequestParam String paymentMethod,
            @RequestParam double amountOrGrams,
            @RequestParam String purpose,
            @RequestParam String status,
            @RequestParam String reason,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .updateLoanDetails(shaftAssignmentId, loanName, paymentMethod, amountOrGrams, purpose, status, reason,
                        principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    private final ShaftAssignmentService shaftAssignmentService;

    @PostMapping
    public ResponseEntity<ShaftAssignment> create(@RequestBody ShaftAssignment shaftAssignment, Principal principal) {
        return ResponseEntity.ok(shaftAssignmentService.create(shaftAssignment, principal));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ShaftAssignment>> getAllApproved() {
        List<ShaftAssignment> assignments = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .getAllApproved();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShaftAssignment> getById(@PathVariable String id) {
        ShaftAssignment assignment = shaftAssignmentService.getById(id);
        return assignment != null ? ResponseEntity.ok(assignment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-miner/{minerId}")
    public ResponseEntity<List<ShaftAssignment>> getByMinerId(@PathVariable String minerId) {
        List<ShaftAssignment> assignments = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .getByMinerId(minerId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping
    public ResponseEntity<List<ShaftAssignment>> getAll() {
        return ResponseEntity.ok(shaftAssignmentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShaftAssignment> update(@PathVariable String id,
            @RequestBody ShaftAssignment shaftAssignment,
            Principal principal) {
        ShaftAssignment updated = shaftAssignmentService.update(id, shaftAssignment, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        shaftAssignmentService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/pushback")
    public ResponseEntity<ShaftAssignment> pushBack(@PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .pushBack(id, reason, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ShaftAssignment> approve(@PathVariable String id, Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .approve(id, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
