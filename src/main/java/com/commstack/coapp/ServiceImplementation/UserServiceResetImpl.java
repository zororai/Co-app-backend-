package com.commstack.coapp.ServiceImplementation;

import com.commstack.coapp.Models.UserEntity;
import com.commstack.coapp.Repositories.UserResetRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import static org.apache.commons.compress.harmony.pack200.PackingUtils.log;

@Service
@RequiredArgsConstructor
public class UserServiceResetImpl {
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;
    // private final HttpHeadersImpl httpHeaders;

    @Autowired
    MongoTemplate mongoTemplate;
    private static final long EXPIRE_TOKEN_AFTER_MINUTES = 30;

    @Autowired
    private UserResetRepo userResetRepository;

    public ResponseEntity<String> forgotPassword(String email) {

        System.out.println("This is the email" + email);
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("email").is(email));
        System.out.println("This is the query" + query1);
        Update updateDef = new Update();

        // Optional<UserEntity> userOptional = Optional
        // .ofNullable(userResetRepository.findByEmail(email));

        if (!mongoTemplate.exists(query1, UserEntity.class)) {
            return ResponseEntity.status(401).body("Invalid email id.");
        }

        var res = mongoTemplate.findOne(query1, UserEntity.class);
        // UserEntity user = userOptional.get();
        res.setToken(String.valueOf(updateDef.set("token", generateToken())));

        updateDef.set("resetTokenCreationDate", LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId()));

        mongoTemplate.findAndModify(query1, updateDef, new FindAndModifyOptions().returnNew(true), UserEntity.class);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testtinappmobile@gmail.com");
        message.setTo(email);
        message.setSubject("Password Reset");
        message.setText("A device has requested to reset your password. Your reset token is "
                + res.getToken().substring(22, res.getToken().indexOf("}"))
                + ". If this is not you, kindly contact your administrator.");
        emailSender.send(message);

        return ResponseEntity.status(200).body(res.getToken());
    }

    public ResponseEntity<String> resetPassword(String token, String password) {

        Optional<UserEntity> userOptional = Optional
                .ofNullable(userResetRepository.findByToken(token));

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("token").is(token));
        Update updateDef = new Update();

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(401).body("Invalid token.");
        }

        LocalDateTime resetTokenCreationDate = userOptional.get().getResetTokenCreationDate();

        if (isTokenExpired(resetTokenCreationDate)) {
            return ResponseEntity.status(401).body("Token expired.");

        }

        UserEntity user = userOptional.get();

        // user.setPassword(passwordEncoder.encode(password));
        // .password(passwordEncoder.encode(password));

        updateDef.set("password", passwordEncoder.encode(password));
        // user.setToken(null);
        updateDef.set("token", null);
        // user.setTokenCreationDate(null);
        updateDef.set("resetTokenCreationDate", null);

        // userRepository.save(user);
        mongoTemplate.findAndModify(query1, updateDef, new FindAndModifyOptions().returnNew(true), UserEntity.class);
        return ResponseEntity.status(200).body("Your password has been successfully updated.");
    }

    /**
     * Generate unique token. You may add multiple parameters to create a strong
     * token.
     *
     * @return unique token
     */
    private String generateToken() {
        StringBuilder token = new StringBuilder();

        return token.append(UUID.randomUUID())
                .append(UUID.randomUUID()).toString();
    }

    /**
     * Check whether the created token expired or not.
     *
     * @param resetTokenCreationDate
     * @return true or false
     */
    private boolean isTokenExpired(final LocalDateTime resetTokenCreationDate) {

        LocalDateTime now = LocalDateTime.now(TimeZone.getTimeZone("GMT+02:00").toZoneId());
        Duration diff = Duration.between(resetTokenCreationDate, now);

        return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
    }
}
