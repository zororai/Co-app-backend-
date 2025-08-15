package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Taxdidection;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface TaxdidectionService {

    List<Taxdidection> getAllApproved();

    List<Taxdidection> getAllRejected();

    List<Taxdidection> getAllPushedBack();

    ResponseEntity<Taxdidection> create(Taxdidection taxdidection, Principal principal);

    List<Taxdidection> getAll();

    Taxdidection getById(String id);

    ResponseEntity<Taxdidection> update(String id, Taxdidection taxdidection, Principal principal);

    ResponseEntity<Taxdidection> delete(String id, Principal principal);

    ResponseEntity<Taxdidection> approve(String id, Principal principal);

    ResponseEntity<Taxdidection> reject(String id, String reason, Principal principal);

    ResponseEntity<Taxdidection> pushBack(String id, String reason, Principal principal);
}
