package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.ShaftAssignment;
import com.commstack.coapp.Service.ShaftAssignmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
@RequestMapping("/api/shaft-assignments")
@RequiredArgsConstructor
public class ShaftAssignmentController {
    private final ShaftAssignmentService shaftAssignmentService;

    @PostMapping
    public ResponseEntity<ShaftAssignment> create(@RequestBody ShaftAssignment shaftAssignment, Principal principal) {
        return ResponseEntity.ok(shaftAssignmentService.create(shaftAssignment, principal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShaftAssignment> getById(@PathVariable String id) {
        ShaftAssignment assignment = shaftAssignmentService.getById(id);
        return assignment != null ? ResponseEntity.ok(assignment) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ShaftAssignment>> getAll() {
        return ResponseEntity.ok(shaftAssignmentService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShaftAssignment> update(@PathVariable String id, @RequestBody ShaftAssignment shaftAssignment,
            Principal principal) {
        ShaftAssignment updated = shaftAssignmentService.update(id, shaftAssignment, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, Principal principal) {
        shaftAssignmentService.delete(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pushback")
    public ResponseEntity<ShaftAssignment> pushBack(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .pushBack(id, reason, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ShaftAssignment> approve(@PathVariable String id, Principal principal) {
        ShaftAssignment updated = ((com.commstack.coapp.ServiceImplementation.ShaftAssignmentServiceImpl) shaftAssignmentService)
                .approve(id, principal);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
