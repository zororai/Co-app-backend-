package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
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
    private LocalDate registrationDate;
    private String email;
    private String position;
    private String role;

    private String status; // PENDING, APPROVED, REJECTED, PUSHED_BACK
    private String reason; // For rejection or pushback

    // Audit fields
    private String createdBy;
    private String updatedBy;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
