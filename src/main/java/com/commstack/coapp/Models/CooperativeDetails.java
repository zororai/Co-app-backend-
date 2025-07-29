package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CooperativeDetails {
    private String cooperativeName;
    private String registrationNumber;
    private LocalDate registrationDate;
    private String operationalStatus;
    private String address;
    private String contactNumber;
    private String email;
}
