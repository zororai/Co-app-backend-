package com.commstack.coapp.DTO;

import java.time.LocalDate;
import java.util.List;

import com.commstack.coapp.Models.CooperativeDetails;
import com.commstack.coapp.Models.TeamMember;

public class RegminerDTO {

    private String name;
    private String surname;
    private String nationIdNumber;
    private String address;
    private String cellNumber;
    private String email;
    private String status;
    private String registrationNumber;
    private LocalDate registrationDate;
    private List<CooperativeDetails> cooperativeDetails;
    private String position;
    private String idPicture;

    private List<TeamMember> teamMembers;

}
