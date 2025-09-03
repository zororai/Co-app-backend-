package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportCost {
    private String id;
    private String paymentMethod;
    private double amountOrGrams;
    private String status;
    private String reason;
}
