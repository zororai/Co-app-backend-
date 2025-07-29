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
@Document(collection = "drivers")
public class Driver {
    @Id
    private String id;
    private String name;
    private String surname;
    private String idNumber;
    private String address;
    private String cellNumber;
    private String email;
    private String password;
    private String companyName;
    private String licenseNumber;
    private String position;
    private LocalDate startContractDate;

    // Document uploads
    private String driverLicense;
    private String idCopy;
    private String medicalCertificate;
    private String defensiveDrivingCertificate;
    private String profilePhoto;

    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
