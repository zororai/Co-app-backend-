package com.commstack.coapp.Service;

import com.commstack.coapp.DTO.DeleteAccountDao;
import com.commstack.coapp.Models.*;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface AuthenticationService {
    ResponseEntity<Object> signup(Signup request, Principal principal);

    ResponseEntity<Object> signin(Signin request);

    ResponseEntity<String> deleteAccount(String email, DeleteAccountDao deleteAccountDao, Principal principal);

    ResponseEntity<String> updateRole(String email, List<Roles> roles, Principal principal);

    ResponseEntity<String> updateUserDetails(String email, String fullName, String oldEmail, Principal principal);

    ResponseEntity<List<UserEntity>> getAll();

    ResponseEntity<List<UserUpdates>> userAuditTrail();

    ResponseEntity<List<UserUpdates>> userAuditTrailByUser(String userEmail);
}
