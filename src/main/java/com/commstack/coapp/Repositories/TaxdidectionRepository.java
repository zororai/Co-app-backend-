package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Taxdidection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxdidectionRepository extends MongoRepository<Taxdidection, String> {
    // Custom query methods if needed
}
