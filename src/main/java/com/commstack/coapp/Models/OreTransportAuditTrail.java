package com.commstack.coapp.Models;

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
@Document(collection = "ore_transport_audit_trail")
public class OreTransportAuditTrail {
    private String transportId;
    private String action;
    private String description;
    private String doneBy;
    private LocalDateTime dateTime;
}
