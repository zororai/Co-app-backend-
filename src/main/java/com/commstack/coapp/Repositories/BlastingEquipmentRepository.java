package com.commstack.coapp.Repositories;

import com.commstack.coapp.Models.BlastingEquipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlastingEquipmentRepository extends MongoRepository<BlastingEquipment, String> {
    List<BlastingEquipment> findByEquipmentType(String equipmentType);

    List<BlastingEquipment> findByManufacturer(String manufacturer);

    List<BlastingEquipment> findByStatus(String status);

    List<BlastingEquipment> findByAssignedTo(String assignedTo);

    List<BlastingEquipment> findByCurrentLocation(String location);

    List<BlastingEquipment> findByMaintenanceStatus(String maintenanceStatus);

    List<BlastingEquipment> findByOperationalStatus(String operationalStatus);

    List<BlastingEquipment> findByNextMaintenanceDateBefore(LocalDate date);

    List<BlastingEquipment> findByNextCalibrationDateBefore(LocalDate date);

    List<BlastingEquipment> findByCertificationExpiryDateBefore(LocalDate date);
}
