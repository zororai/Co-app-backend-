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
@Document(collection = "mill_onboarding")
public class MillOnboarding {
    @Id
    private String id;
    private String millId; // Assuming millId is a unique identifier for the mill
    private String millName;
    private String millLocation;
    private String millType;
    private String owner;
    private boolean activeStatus;
    private String idNumber;
    private String address;
    private String picture;
    private String status;
    private String reason;
    private String statusHealth;
    private String createdBy;
    private String createdDate;
    private String updatedBy;
    private String updatedDate;

}
