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
@Document(collection = "vehicles")
public class Vehicle {
    @Id
    private String id;
    private String assignedDriver;
    private String regNumber;
    private String ownerName;
    private String ownerAddress;
    private String ownerCellNumber;
    private String ownerIdNumber;
    private String vehicleType;
    private String year;
    private String make;
    private String operationalStatus;
    private String model;
    private LocalDate lastServiceDate;

    // Document uploads
    private String idPicture;
    private String truckPicture;
    private String registrationBook;

    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
