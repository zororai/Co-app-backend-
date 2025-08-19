package com.commstack.coapp.Service;

import com.commstack.coapp.Models.ProductionLoan;
import org.springframework.http.ResponseEntity;
import java.security.Principal;
import java.util.List;

public interface ProductionLoanService {
    ResponseEntity<ProductionLoan> create(ProductionLoan productionLoan, Principal principal);

    List<ProductionLoan> getAll();

    ProductionLoan getById(String id);

    ResponseEntity<ProductionLoan> approve(String id, Principal principal);

    ResponseEntity<ProductionLoan> reject(String id, String reason, Principal principal);

    ResponseEntity<ProductionLoan> pushBack(String id, String reason, Principal principal);

    ResponseEntity<ProductionLoan> update(String id, ProductionLoan productionLoan, Principal principal);

    ResponseEntity<String> delete(String id, Principal principal);
}
