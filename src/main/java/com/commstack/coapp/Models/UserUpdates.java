package com.commstack.coapp.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_audit_trail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserUpdates {
    private String doneBy;
    private LocalDateTime dateTime;
    private String description;
}
