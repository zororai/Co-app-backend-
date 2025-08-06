package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "shaft_assignments")
public class ShaftAssignment {
    @Id
    private String id;
    private String minerId;
    private String sectionName;
    private String shaftNumbers;
    private BigDecimal medicalFee;
    private BigDecimal regFee;
    private int shaftNumber;
    private LocalDate startContractDate;
    private LocalDate endContractDate;
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;

}
