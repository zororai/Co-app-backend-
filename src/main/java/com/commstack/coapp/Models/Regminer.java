package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reg_miner")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Regminer {
    @Id
    private String id; // MongoDB will generate this if not provided

    private String name;
    private String surname;
    private String nationIdNumber;
    private String address;
    private String cellNumber;
    private int shaftnumber;
    private String email;
    private String status;
    private String reason;
    private String registrationNumber;
    private LocalDate registrationDate;
    private List<CooperativeDetails> cooperativeDetails;
    private String position;
    private String idPicture;
    private List<TeamMember> teamMembers;

    private String createdby;
    private String updatedby;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}
