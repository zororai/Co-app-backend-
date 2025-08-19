package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "production_loan")
public class ProductionLoan {
    @Id
    private String id;
    private String loanName;
    private String paymentMethod;
    private double amountOrGrams;
    private String purpose;
    private String status;
    private String reason;
}
