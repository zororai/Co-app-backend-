package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blasting_equipment")
public class BlastingEquipment {
    @Id
    private String id;

    private String equipmentName;
    private String equipmentType;
    private String serialNumber;
    private String manufacturer;
    private String model;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String maintenanceStatus;
    private String currentLocation;
    private String assignedTo;
    private String condition;
    private String calibrationStatus;
    private LocalDate lastCalibrationDate;
    private LocalDate nextCalibrationDate;
    private String certificationNumber;
    private LocalDate certificationExpiryDate;
    private String safetyRating;
    private String operationalStatus;
    private String notes;

    // Audit fields
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
