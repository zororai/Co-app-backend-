package com.commstack.coapp.Service;

import com.commstack.coapp.Models.SecurityCompanyEmployee;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface SecurityCompanyEmployeeService {
    ResponseEntity<String> create(SecurityCompanyEmployee employee);

    List<SecurityCompanyEmployee> getAll();

    SecurityCompanyEmployee getById(String id);

    ResponseEntity<String> update(String id, SecurityCompanyEmployee employee);

    ResponseEntity<String> delete(String id);
}
