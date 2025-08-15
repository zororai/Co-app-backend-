package com.commstack.coapp.Models;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tax {

    private String id;
    private String taxType;
    private double taxRate;
    private String location;
    private String description;
    private String status;

}
