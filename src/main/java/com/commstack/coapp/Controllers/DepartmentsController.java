package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.Departments;
import com.commstack.coapp.Service.DepartmentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentsController {

    private final DepartmentsService departmentsService;

    public DepartmentsController(DepartmentsService departmentsService) {
        this.departmentsService = departmentsService;
    }

    @PostMapping
    public ResponseEntity<Departments> create(@RequestBody Departments departments, Principal principal) {
        Departments created = departmentsService.create(departments, principal);
        return ResponseEntity.created(URI.create("/api/departments/" + created.getId())).body(created);
    }

    @GetMapping
    public List<Departments> list() {
        return departmentsService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departments> getById(@PathVariable String id) {
        Departments d = departmentsService.getById(id);
        return d == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(d);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Departments> update(@PathVariable String id, @RequestBody Departments departments,
            Principal principal) {
        Departments updated = departmentsService.update(id, departments, principal);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        departmentsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<Departments> getByCode(@PathVariable String code) {
        Departments d = departmentsService.findByCode(code);
        return d == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(d);
    }
}
