package com.commstack.coapp.DTO;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.commstack.coapp.Models.Tax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OreTransportDTO {
    private String shaftNumbers;
    private double weight;
    private int numberOfBags;
    private double newWeight;
    private int newnumberOfBags;
    private String transportStatus;
    private String dedicationReason;
    private List<Tax> tax;
    private String processStatus;
    private String location;

}
