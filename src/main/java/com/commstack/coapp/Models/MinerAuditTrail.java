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
@Document(collection = "miner_audit_trail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MinerAuditTrail {
    private String minerId;
    private String action;
    private String description;
    private String doneBy;
    private LocalDateTime dateTime;
}
