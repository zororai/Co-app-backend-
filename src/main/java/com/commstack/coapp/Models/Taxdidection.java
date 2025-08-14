package com.commstack.coapp.Models;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tax_deductions")
public class Taxdidection {
    private String id;
    private String taxType;
    private double taxRate;
    private String location;
    private String description;
}
