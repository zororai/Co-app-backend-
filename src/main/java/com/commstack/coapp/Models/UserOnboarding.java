package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_onboarding")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOnboarding {
    @Id
    private String id;

    private String name;
    private String password;
    private String surname;
    private String idNumber;
    private String address;
    private String cellNumber;
    private String email;
    private String position;
    private String location;
    private String role;
    // private List<String> permissions;
    private String status;
    private String notes;
    private String reason;
    // Audit fields
    private String createdBy;
    private String updatedBy;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
