
package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Vehicle;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface VehicleService {

    ResponseEntity<String> setToInTransit(String id, Principal principal);

    ResponseEntity<String> setToLoading(String id, Principal principal);

    ResponseEntity<String> setToIdle(String id, Principal principal);

    ResponseEntity<String> setToMaintainance(String id, Principal principal);

    ResponseEntity<String> create(Vehicle vehicle, Principal principal);

    List<Vehicle> getAll();

    Vehicle getById(String id);

    List<Vehicle> getAllIdleVehicles();

    ResponseEntity<String> update(String id, Vehicle vehicle, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approval(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);

    ResponseEntity<String> getAllPendingVehicles();

    ResponseEntity<String> getAllApprovedVehicles();

    ResponseEntity<String> getAllRejectedVehicles();

    ResponseEntity<String> getAllPushedBackVehicles();

    Vehicle findByRegNumber(String regNumber);

    List<Vehicle> findByOwnerName(String ownerName);

    Vehicle findByOwnerIdNumber(String ownerIdNumber);

    long getApprovedVehicleCount();
}
