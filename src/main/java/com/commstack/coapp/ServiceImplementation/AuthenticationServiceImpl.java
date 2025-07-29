package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.DTO.DeleteAccountDao;
import com.commstack.coapp.Models.*;
import com.commstack.coapp.Repositories.UserRepository;
import com.commstack.coapp.Service.AuthenticationService;
import com.commstack.coapp.Service.JwtService;
import lombok.RequiredArgsConstructor;
//import org.json.JSONObject;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MongoTemplate mongoTemplate;
    private final JavaMailSender emailSender;
    // private final HttpHeadersImpl httpHeaders;

    @Override
    public ResponseEntity<Object> signup(Signup request, Principal principal) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // for (Roles role : request.getRole()){
        // role.setAssignedBy(auth.getName());
        // role.setAssignedAt(LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId()));
        // }

        var user = UserEntity.builder().fullName(request.getFullName()).deleted(false).createdBy("zoror")
                .userCreatedAt(LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId()))
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).build();
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {

            Query query1 = new Query();
            query1.addCriteria(Criteria.where("email").is(request.getEmail()));
            var user1 = mongoTemplate.findOne(query1, UserEntity.class);

            if (user1.getDeleted()) {
                user.setReinstatedBy(auth.getName());
                user.setReinstateTime(LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId()));
                mongoTemplate.findAndReplace(query1, user, "users");
                var jwt = jwtService.generateToken(user);

                return ResponseEntity.status(200).body(JwtAuthenticationResponse.builder().token(jwt).build());
            }
            return ResponseEntity.status(401).body("User already exists.");
        }
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        var userUpdates = UserUpdates.builder().description("User " + request.getEmail() + " was created.")
                .dateTime(LocalDateTime.now()).doneBy("zororai").build();
        mongoTemplate.save(userUpdates);

        return ResponseEntity.status(200).body(JwtAuthenticationResponse.builder().token(jwt).build());
    }

    @Override
    public ResponseEntity<Object> signin(Signin request) {

        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (user.getDeleted()) {
            return ResponseEntity.status(401).body("Your account has been deleted. Please contact your administrator.");
        }

        if (user.getToken() != null) {
            return ResponseEntity.status(401).body(
                    "Your account has been flagged for a password change. Please change your password and log in again.");
        }

        var jwt = jwtService.generateToken(user);

        return ResponseEntity.status(200)
                .body(JwtAuthenticationResponse.builder().token(jwt).email(request.getEmail()).password("")
                        .deleted(false).fullName(user.getFullName())// .role(user.getRole())
                        .build());
    }

    @Override
    public ResponseEntity<String> deleteAccount(String email, DeleteAccountDao deleteAccountDao, Principal principal) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        Update update = new Update();
        update.set("reason", deleteAccountDao.getComment());
        update.set("deleted", true);
        update.set("deletedBy", principal.getName());
        update.set("deleteTime", LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId()));

        var userUpdates = UserUpdates.builder().description("User " + email + " was deleted.")
                .dateTime(LocalDateTime.now()).doneBy(principal.getName()).build();
        mongoTemplate.save(userUpdates);

        return ResponseEntity.status(200)
                .body(String.valueOf(mongoTemplate.findAndModify(query, update, UserEntity.class, "users")));
    }

    @Override
    public ResponseEntity<String> updateRole(String email, List<Roles> roles, Principal principal) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        for (Roles role : roles) {
            role.setAssignedBy(principal.getName());
            role.setAssignedAt(LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId()));
        }

        var oldRoles = mongoTemplate.findOne(query, UserEntity.class);
        Update update = new Update();
        update.set("role", roles);
        update.set("roleUpdatedBy", principal.getName());

        // var userUpdates = UserUpdates.builder().description("User "+ email + " had
        // their roles updated from " + oldRoles.getRoles + "to " +
        // roles).dateTime(LocalDateTime.now()).doneBy(principal.getName()).build();
        // mongoTemplate.save(userUpdates);

        return ResponseEntity.status(200).body(String.valueOf(mongoTemplate.findAndModify(query, update,
                new FindAndModifyOptions().returnNew(true), UserEntity.class, "users")));
    }

    @Override
    public ResponseEntity<String> updateUserDetails(String newEmail, String fullName, String email,
            Principal principal) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));

        if (!mongoTemplate.exists(query, UserEntity.class)) {
            return ResponseEntity.status(401).body("Please enter update details and try again");
        }

        if (!Objects.equals(newEmail, "") && !Objects.equals(fullName, "")) {
            Update update = new Update();
            update.set("email", newEmail);
            update.set("fullName", fullName);
            mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), UserEntity.class,
                    "users");

            var userUpdates = UserUpdates.builder()
                    .description("User details " + email + " were updated to " + newEmail + " " + fullName + ".")
                    .dateTime(LocalDateTime.now()).doneBy(principal.getName()).build();
            mongoTemplate.save(userUpdates);

            return ResponseEntity.status(200).body("User updated.");
        }
        if (!Objects.equals(fullName, "")) {
            Update update = new Update();
            update.set("fullName", fullName);
            mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), UserEntity.class,
                    "users");

            var userUpdates = UserUpdates.builder().description("User's name " + fullName + " was updated.")
                    .dateTime(LocalDateTime.now()).doneBy(principal.getName()).build();
            mongoTemplate.save(userUpdates);

            return ResponseEntity.status(200).body("Full name updated");
        }
        if (!Objects.equals(email, "") && !Objects.equals(newEmail, "")) {
            Update update = new Update();
            update.set("email", newEmail);
            mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), UserEntity.class,
                    "users");

            var userUpdates = UserUpdates.builder().description("User's email " + email + " was updated to " + newEmail)
                    .dateTime(LocalDateTime.now()).doneBy(principal.getName()).build();
            mongoTemplate.save(userUpdates);

            return ResponseEntity.status(200).body("Email updated.");
        }

        return ResponseEntity.status(401).body("Please enter update details and try again");
    }

    @Override
    public ResponseEntity<List<UserEntity>> getAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        for (UserEntity userEntity : userEntities) {
            userEntity.setToken("");
            userEntity.setPassword("");
        }
        return ResponseEntity.status(200).body(userEntities);
    }

    public ResponseEntity<List<UserUpdates>> userAuditTrail() {

        return ResponseEntity.status(200).body(mongoTemplate.findAll(UserUpdates.class));
    }

    public ResponseEntity<List<UserUpdates>> userAuditTrailByUser(String userEmail) {

        Query query = new Query();
        query.addCriteria(Criteria.where("doneBy").is(userEmail));

        return ResponseEntity.status(200).body(mongoTemplate.find(query, UserUpdates.class));
    }
}
