package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "blast_schedules")
public class BlastSchedule {
    @Id
    private String id;
    private String eventTitle;
    private String blastType;
    private LocalDate date;
    private LocalTime time;
    private Double duration;
    private String blastZone;
    private String specificLocation;
    private String assignedOperator;
    private String safetyOfficer;
    private String explosiveType;
    private Double quantity;
    private Double evacuationRadius;

    // Departments to notify
    private boolean notifySHE;
    private boolean notifyTransport;
    private boolean notifySecurity;
    private boolean notifyMaintenance;
    private boolean notifyRecoveryTeam;
    private boolean notifyEnvironmental;

    // Requirements
    private String weatherRequirements;

    // Status tracking
    private String status;
    private String reason;
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
