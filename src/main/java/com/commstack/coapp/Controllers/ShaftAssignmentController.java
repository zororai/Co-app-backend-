
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
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ShaftAssignmentController {

    @GetMapping("/shaft-numbers/by-section/{sectionName}")
    public ResponseEntity<List<String>> getShaftNumbersBySectionName(@PathVariable String sectionName) {
        List<String> shaftNumbers = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .getShaftNumbersBySectionName(sectionName);
        String generated;
        if (sectionName != null && sectionName.length() >= 3) {
            if (shaftNumbers.isEmpty()) {
                generated = sectionName.substring(0, 2) + sectionName.charAt(sectionName.length() - 1) + "1";
            } else {
                // Find max numeric part in shaftNumbers
                int maxNum = shaftNumbers.stream()
                        .map(sn -> sn.replaceAll("[^0-9]", ""))
                        .filter(s -> !s.isEmpty())
                        .mapToInt(Integer::parseInt)
                        .max().orElse(0);
                generated = sectionName.substring(0, 2) + sectionName.charAt(sectionName.length() - 1) + (maxNum + 1);
            }
            shaftNumbers = List.of(generated);
        }
        return ResponseEntity.ok(shaftNumbers);
    }

    @GetMapping("/by-shaft-number/{shaftNumbers}/loans")
    public ResponseEntity<List<com.commstack.coapp.Models.Loan>> getLoansByShaftNumbers(
            @PathVariable String shaftNumbers) {
        List<com.commstack.coapp.Models.Loan> loans = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .getLoansByShaftNumbers(shaftNumbers);
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/{id}/loan/payment")
    public ResponseEntity<ShaftAssignment> updateLoanPayment(
            @PathVariable("id") String shaftAssignmentId,
            @RequestParam double amountPaid,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .updateLoanPayment(shaftAssignmentId, amountPaid, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/loan/pushback")
    public ResponseEntity<ShaftAssignment> pushBackLoan(@PathVariable("id") String shaftAssignmentId,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .pushBackLoan(shaftAssignmentId, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/loan/reject")
    public ResponseEntity<ShaftAssignment> rejectLoan(@PathVariable("id") String shaftAssignmentId,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .rejectLoan(shaftAssignmentId, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/loan/approve")
    public ResponseEntity<ShaftAssignment> approveLoan(@PathVariable("id") String shaftAssignmentId,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .approveLoan(shaftAssignmentId, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

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
    public org.springframework.http.ResponseEntity<?> create(@RequestBody ShaftAssignment shaftAssignment,
            Principal principal) {
        return shaftAssignmentService.create(shaftAssignment, principal);
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

    @PutMapping("/{id}/suspend-for-she")
    public ResponseEntity<ShaftAssignment> suspendForSHE(@PathVariable String id,
            @RequestParam String reason,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .suspendForSHE(id, reason, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @GetMapping("/status-counts")
    public ResponseEntity<com.commstack.coapp.DTO.ShaftStatusCountsDTO> getStatusCounts() {
        com.commstack.coapp.DTO.ShaftStatusCountsDTO counts = shaftAssignmentService.getSuspendedAndApprovedCounts();
        return ResponseEntity.ok(counts);
    }
}
