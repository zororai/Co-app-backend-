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
// ...existing code...
import com.commstack.coapp.Models.Tax;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ore_transports")

public class OreTransport {
    @Id
    private String id;
    private String oreUniqueId;
    private List<String> shaftNumbers;
    private double weight;
    private int numberOfBags;
    private String transportStatus;
    private String selectedTransportdriver;
    private String selectedTransport;
    private List<Tax> tax;
    private String transportReason;
    private String processStatus;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String createdBy;
    private String updatedBy;
    private LocalDate createdDate;
    private LocalDate updatedDate;
}
