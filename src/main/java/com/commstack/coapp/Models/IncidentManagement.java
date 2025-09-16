package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "incident_management")
public class IncidentManagement {
    @Id
    private String id;

    private String incidentTitle;
    private String severityLevel;
    private String reportedBy;
    private String description;
    private List<String> attachments; // e.g., file paths or URLs
    private String location;

    private List<Person> participants;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {
        private String name;
        private String surname;
        private String nationalId;
        private String address;
    }
}
