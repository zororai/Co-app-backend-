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
@Document(collection = "security_companies")
public class SecurityCompany {
    @Id
    private String id;
    private String companyName;
    private String bpNumber;
    private String ownerIdNumber;
    private String ownerName;
    private String ownerSurname;
    private String ownerAddress;
    private String numberOfEmployees;
    private String companyAddress;
    private String position;
    private LocalDate startContractDate;
    private String emailAddress;
    private String password;
    private String ownerCellNumber;

    // Document uploads
    private String validTaxClearance;
    private String companyLogo;
    private String cr14Copy;
    private String operatingLicense;

    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
