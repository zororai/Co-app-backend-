package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.Taxdidection;
import com.commstack.coapp.Service.TaxdidectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.*;

@Service
public class TaxdidectionServiceImpl implements TaxdidectionService {
    private final Map<String, Taxdidection> store = new HashMap<>();

    @Override
    public ResponseEntity<String> create(Taxdidection taxdidection, Principal principal) {
        String id = UUID.randomUUID().toString();
        taxdidection.setId(id);
        store.put(id, taxdidection);
        return ResponseEntity.ok("Taxdidection created successfully");
    }

    @Override
    public List<Taxdidection> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Taxdidection getById(String id) {
        return store.get(id);
    }

    @Override
    public ResponseEntity<String> update(String id, Taxdidection taxdidection, Principal principal) {
        if (!store.containsKey(id)) {
            return ResponseEntity.status(404).body("Taxdidection not found");
        }
        taxdidection.setId(id);
        store.put(id, taxdidection);
        return ResponseEntity.ok("Taxdidection updated successfully");
    }

    @Override
    public ResponseEntity<String> delete(String id, Principal principal) {
        if (store.remove(id) != null) {
            return ResponseEntity.ok("Taxdidection deleted successfully");
        }
        return ResponseEntity.status(404).body("Taxdidection not found");
    }

    @Override
    public ResponseEntity<String> approve(String id, Principal principal) {
        Taxdidection tax = store.get(id);
        if (tax == null) {
            return ResponseEntity.status(404).body("Taxdidection not found");
        }
        // Add approval logic here (e.g., set a status field if needed)
        return ResponseEntity.ok("Taxdidection approved");
    }

    @Override
    public ResponseEntity<String> reject(String id, String reason, Principal principal) {
        Taxdidection tax = store.get(id);
        if (tax == null) {
            return ResponseEntity.status(404).body("Taxdidection not found");
        }
        // Add rejection logic here (e.g., set a status and reason field if needed)
        return ResponseEntity.ok("Taxdidection rejected: " + reason);
    }

    @Override
    public ResponseEntity<String> pushBack(String id, String reason, Principal principal) {
        Taxdidection tax = store.get(id);
        if (tax == null) {
            return ResponseEntity.status(404).body("Taxdidection not found");
        }
        // Add push back logic here (e.g., set a status and reason field if needed)
        return ResponseEntity.ok("Taxdidection pushed back: " + reason);
    }
}
