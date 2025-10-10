package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Departments;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentsRepository extends MongoRepository<Departments, String> {
    Optional<Departments> findByCode(String code);
}
