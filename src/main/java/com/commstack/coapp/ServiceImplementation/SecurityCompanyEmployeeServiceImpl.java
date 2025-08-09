package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.SecurityCompanyEmployee;
import com.commstack.coapp.Repositories.SecurityCompanyEmployeeRepository;
import com.commstack.coapp.Service.SecurityCompanyEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SecurityCompanyEmployeeServiceImpl implements SecurityCompanyEmployeeService {

    @Autowired
    private SecurityCompanyEmployeeRepository repository;

    @Override
    public ResponseEntity<String> create(SecurityCompanyEmployee employee) {
        repository.save(employee);
        return ResponseEntity.ok("Employee created successfully");
    }

    @Override
    public List<SecurityCompanyEmployee> getAll() {
        return repository.findAll();
    }

    @Override
    public SecurityCompanyEmployee getById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ResponseEntity<String> update(String id, SecurityCompanyEmployee employee) {
        Optional<SecurityCompanyEmployee> existing = repository.findById(id);
        if (existing.isPresent()) {
            SecurityCompanyEmployee existingEmployee = existing.get();
            existingEmployee.setName(employee.getName());
            existingEmployee.setSurname(employee.getSurname());
            existingEmployee.setForceNumber(employee.getForceNumber());
            existingEmployee.setLocation(employee.getLocation());
            existingEmployee.setPhoneNumber(employee.getPhoneNumber());
            existingEmployee.setSupervisor(employee.getSupervisor());
            repository.save(existingEmployee);
            return ResponseEntity.ok("Employee updated successfully");
        }
        return ResponseEntity.status(404).body("Employee not found");
    }

    @Override
    public ResponseEntity<String> delete(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok("Employee deleted successfully");
        }
        return ResponseEntity.status(404).body("Employee not found");
    }
}
