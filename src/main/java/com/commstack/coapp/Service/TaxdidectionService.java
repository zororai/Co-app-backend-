package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Taxdidection;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface TaxdidectionService {
    ResponseEntity<String> create(Taxdidection taxdidection, Principal principal);

    List<Taxdidection> getAll();

    Taxdidection getById(String id);

    ResponseEntity<String> update(String id, Taxdidection taxdidection, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);

    ResponseEntity<String> approve(String id, Principal principal);

    ResponseEntity<String> reject(String id, String reason, Principal principal);

    ResponseEntity<String> pushBack(String id, String reason, Principal principal);
}
