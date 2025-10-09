package com.commstack.coapp.Service;

import com.commstack.coapp.Models.IncidentManagement;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface IncidentManagementService {
    ResponseEntity<IncidentManagement> create(IncidentManagement incident, Principal principal);

    List<IncidentManagement> getAll();

    IncidentManagement getById(String id);

    ResponseEntity<IncidentManagement> update(String id, IncidentManagement incident, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<IncidentManagement> resolve(String id, String resolution, Principal principal);
}
