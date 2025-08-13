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
    private String firstName;
    private String lastName;
    private String idNumber;
    private LocalDate dateOfBirth;
    private String licenseNumber;
    private String licenseClass;
    private String licenseDocument;
    private LocalDate licenseExpiryDate;
    private int yearsOfExperience;
    private String phoneNumber;
    private String emailAddress;
    private String address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String driversLicenseCopyPath;
    private String idDocumentCopyPath;
    private String additionalNotes;

    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
