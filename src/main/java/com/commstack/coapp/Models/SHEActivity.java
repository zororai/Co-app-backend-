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
@Document(collection = "she_activities")
public class SHEActivity {
    @Id
    private String id;

    // Basic Information
    private String activityTitle;
    private String activityType; // INSPECTION, AUDIT, TRAINING, DRILL, INCIDENT_INVESTIGATION, RISK_ASSESSMENT
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;

    // Participants and Responsibility
    private String conductedBy;
    private List<String> participants;
    private String department;
    private String supervisor;

    // Risk and Compliance
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private List<String> hazardsIdentified;
    private List<String> controlMeasures;
    private boolean ppeRequired;
    private List<String> requiredPPE;
    private String regulatoryReference;

    // Findings and Actions
    private List<String> observations;
    private List<String> nonConformities;
    private List<String> correctiveActions;
    private List<String> preventiveActions;
    private LocalDate deadlineForActions;
    private String responsiblePerson;

    // Documentation
    private String documentReference;
    private List<String> attachments;
    private List<String> photos;

    // Follow-up
    private boolean followUpRequired;
    private LocalDate followUpDate;
    private String followUpAssignedTo;
    private String followUpStatus;

    // Approval and Status
    private String status; // DRAFT, PENDING, APPROVED, REJECTED, PUSHED_BACK, COMPLETED
    private String reason;
    private String approvalComments;

    // Metrics
    private int safetyScore;
    private int complianceScore;
    private int participationRate;

    // Audit fields
    private String createdBy;
    private LocalDate createdAt;
    private String updatedBy;
    private LocalDate updatedAt;
}
