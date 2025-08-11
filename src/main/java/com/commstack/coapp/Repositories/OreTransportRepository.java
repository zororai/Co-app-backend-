package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.OreTransport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OreTransportRepository extends MongoRepository<OreTransport, String> {
    // Add custom query methods if needed
}
