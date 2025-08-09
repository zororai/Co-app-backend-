package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.SecurityCompanyEmployee;
import com.commstack.coapp.Service.SecurityCompanyEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/security-company-employees")
@CrossOrigin
public class SecurityCompanyEmployeeController {

    @Autowired
    private SecurityCompanyEmployeeService service;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody SecurityCompanyEmployee employee) {
        return service.create(employee);
    }

    @GetMapping
    public List<SecurityCompanyEmployee> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SecurityCompanyEmployee getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody SecurityCompanyEmployee employee) {
        return service.update(id, employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
