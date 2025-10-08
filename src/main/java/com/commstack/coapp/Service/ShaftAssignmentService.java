
package com.commstack.coapp.Service;

import com.commstack.coapp.Models.ShaftAssignment;
import java.util.List;
import java.security.Principal;

public interface ShaftAssignmentService {
    org.springframework.http.ResponseEntity<?> create(ShaftAssignment shaftAssignment, Principal principal);

    ShaftAssignment getById(String id);

    List<ShaftAssignment> getAll();

    List<ShaftAssignment> getAllApproved();

    ShaftAssignment update(String id, ShaftAssignment shaftAssignment, Principal principal);

    void delete(String id, Principal principal);

    ShaftAssignment suspendForSHE(String id, String reason, Principal principal);
}
