package com.commstack.coapp.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Spliterator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity implements UserDetails {
    @Transient
    public static final String SEQUENCE_NAME = "user_sequence";

    String fullName;
    String email;
    String password;
    String token;
    // private List<Roles> role;
    String createdBy;
    String userUpdatedBy;
    private LocalDateTime userUpdatedAt;
    private LocalDateTime resetTokenCreationDate;
    private LocalDateTime userCreatedAt;
    String resetAuthorisedBy;
    Boolean deleted;
    String reason;
    String deletedBy;
    private LocalDateTime deleteTime;
    // String roleUpdatedBy;
    String reinstatedBy;
    private LocalDateTime reinstateTime;

    @JsonDeserialize(using = CustomAuthorityDeserializer.class)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> list = new ArrayList<>();
        // for (Roles role1 : role){
        // list.add(new SimpleGrantedAuthority(role1.getRole()));
        // System.out.println(list);
        // }

        list.add(new SimpleGrantedAuthority("User"));
        return list;
    }

    @Override
    public String getUsername() {
        // email in our case
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
