package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private String id;
    private String loanName;
    private String paymentMethod;
    private double amountOrGrams;
    private String purpose;
    private String paymentStatus;
    private String status;
    private String reason;
}
