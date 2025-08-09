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
@Document(collection = "security_company_employees")
public class SecurityCompanyEmployee {
    @Id
    private String id;
    private String companyId;
    private String name;
    private String surname;

    private String forceNumber;
    private String location;
    private String phoneNumber;
    private String supervisor;
}
