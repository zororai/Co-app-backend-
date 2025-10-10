package com.commstack.coapp.Service;

import com.commstack.coapp.Models.Departments;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface DepartmentsService {
    Departments create(Departments departments, Principal principal);

    List<Departments> getAll();

    Departments getById(String id);

    Departments update(String id, Departments departments, Principal principal);

    void delete(String id);

    Departments findByCode(String code);
}
