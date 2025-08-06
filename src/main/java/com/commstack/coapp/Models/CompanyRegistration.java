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
@Document(collection = "company_registrations")
public class CompanyRegistration {
    @Id
    private String id;
    private String companyName;
    private String address;
    private String cellNumber;
    private String email;
    private String registrationNumber;
    private String companyLogo;
    private int shaftnumber;
    private String certificateOfCooperation;
    private String cr14Copy;
    private String miningCertificate;
    private String taxClearance;
    private String passportPhoto;

    // Owner Details
    private String ownerName;
    private String ownerSurname;
    private String ownerAddress;
    private String ownerCellNumber;
    private String ownerIdNumber;

    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;

}
