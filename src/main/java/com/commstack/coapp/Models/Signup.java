package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signup {
    String fullName;
    private String email;
    private String password;
    // List<Roles> role;
    private LocalDateTime signUpTime;
    private String signedUpBy;
}
