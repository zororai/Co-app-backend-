package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.ProductionLoan;
import com.commstack.coapp.Service.ProductionLoanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/production-loan")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductionLoanController {

    @GetMapping("/approved")
    public List<ProductionLoan> getAllApproved() {
        return service.getAllApproved();
    }

    @Autowired
    private ProductionLoanService service;

    @PostMapping("/create")
    public ResponseEntity<ProductionLoan> create(@RequestBody ProductionLoan productionLoan, Principal principal) {
        return service.create(productionLoan, principal);
    }

    @GetMapping("/all")
    public List<ProductionLoan> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ProductionLoan> approve(@PathVariable String id, Principal principal) {
        return service.approve(id, principal);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ProductionLoan> reject(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        return service.reject(id, reason, principal);
    }

    @PutMapping("/{id}/push-back")
    public ResponseEntity<ProductionLoan> pushBack(@PathVariable String id, @RequestParam String reason,
            Principal principal) {
        return service.pushBack(id, reason, principal);
    }

    @GetMapping("/{id}")
    public ProductionLoan getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductionLoan> update(@PathVariable String id, @RequestBody ProductionLoan productionLoan,
            Principal principal) {
        return service.update(id, productionLoan, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }
}
