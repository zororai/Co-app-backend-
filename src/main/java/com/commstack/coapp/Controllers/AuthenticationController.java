package com.commstack.coapp.Controllers;

import com.commstack.coapp.DTO.DeleteAccountDao;
import com.commstack.coapp.DTO.ResetDao;
import com.commstack.coapp.Models.*;
import com.commstack.coapp.Service.AuthenticationService;
import com.commstack.coapp.ServiceImplementation.UserServiceResetImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "https://coappapi.commapp.online/", allowCredentials = "true")

public class AuthenticationController {
    @Autowired
    private UserServiceResetImpl userServiceResetImpl;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody Signup request, Principal principal) {
        return authenticationService.signup(request, principal);
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@RequestBody Signin request) {
        return authenticationService.signin(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {

        return userServiceResetImpl.forgotPassword(email);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetDao resetDao) {

        return userServiceResetImpl.resetPassword(resetDao.token, resetDao.password);
    }

    @GetMapping("/viewUsers")
    public ResponseEntity<List<UserEntity>> getAll() {
        return authenticationService.getAll();
    }

    @GetMapping("/viewAuditTrail")
    public ResponseEntity<List<UserUpdates>> getAuditTrail() {
        return authenticationService.userAuditTrail();
    }

    @GetMapping("/viewAuditTrailByUser")
    public ResponseEntity<List<UserUpdates>> getAuditTrailByUser(@RequestParam String userEmail) {
        return authenticationService.userAuditTrailByUser(userEmail);
    }

    // @PreAuthorize("hasAuthority('User')")
    // @PutMapping("/updateUserAccount")
    // public ResponseEntity<String> updateRole(@RequestBody UpdateRoleDao
    // updateRoleDao, Principal principal){
    // return authenticationService.updateRole(updateRoleDao.getEmail(),
    // updateRoleDao.getRole(), principal);
    // }

    @PutMapping("/updateUserDetails")
    public ResponseEntity<String> updateUserDetails(@RequestParam Optional<String> fullName,
            @RequestParam Optional<String> newEmail, @RequestParam String email, Principal principal) {

        return authenticationService.updateUserDetails(newEmail.orElse(""), fullName.orElse(""), email, principal);
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountDao deleteAccountDao, Principal principal) {
        return authenticationService.deleteAccount(deleteAccountDao.getEmail(), deleteAccountDao, principal);
    }
}
