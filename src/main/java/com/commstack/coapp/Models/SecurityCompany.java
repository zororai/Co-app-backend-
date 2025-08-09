package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "security_companies")
public class SecurityCompany {
    @Id
    private String id;
    private String companyName;
    private String registrationNumber;
    private String contactPhone;
    private String contactPersonName;
    private String contactEmail;
    private String siteAddress;
    private String serviceType;
    private String headOfficeAddress;
    private String numberOfWorks; // Lombok @Data should generate getter/setter
    private LocalDate startContractDate;
    private LocalDate endContractDate;
    private String emergencyContactPhone;
    private String emergencyContactName;
    private List<String> locations;
    // Document uploads
    private String validTaxClearance;
    private String companyLogo;
    private String getCertificateOfCooperation;
    private String operatingLicense;
    private String proofOfInsurance;
    private String siteRiskAssessmentReport;
    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
