package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OreSample {

    private String id;
    private String sampleType;
    private String sampleWeight;

    private double result;
    private String status;
    private String reason;
}
