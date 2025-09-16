package com.commstack.coapp.Controllers;

import com.commstack.coapp.Models.IncidentManagement;
import com.commstack.coapp.Service.IncidentManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/incident-management")
@SecurityRequirement(name = "Bearer Authentication")
public class IncidentManagementController {

    @Autowired
    private IncidentManagementService service;

    @PostMapping("/create")
    public ResponseEntity<IncidentManagement> create(@RequestBody IncidentManagement incident, Principal principal) {
        return service.create(incident, principal);
    }

    @GetMapping("/all")
    public List<IncidentManagement> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public IncidentManagement getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncidentManagement> update(@PathVariable String id,
                                                     @RequestBody IncidentManagement incident,
                                                     Principal principal) {
        return service.update(id, incident, principal);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id, Principal principal) {
        return service.delete(id, principal);
    }
}
