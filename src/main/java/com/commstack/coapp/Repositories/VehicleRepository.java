package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.Vehicle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    List<Vehicle> findByStatus(String status);

    Vehicle findByRegNumber(String regNumber);

    List<Vehicle> findByOwnerName(String ownerName);

    Vehicle findByOwnerIdNumber(String ownerIdNumber);
}
