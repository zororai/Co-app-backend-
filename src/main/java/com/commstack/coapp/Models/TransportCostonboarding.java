package com.commstack.coapp.Models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "transport_cost_onboarding")
public class TransportCostonboarding {
    @Id
    private String id;
    private String paymentMethod;
    private double amountOrGrams;
    private String status;
    private String reason;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
