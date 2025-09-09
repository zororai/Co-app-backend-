package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import com.commstack.coapp.Models.Tax;
import com.commstack.coapp.Models.OreSample;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ore")

public class OreTransport {
    @Id
    private String id;
    private String oreUniqueId;
    private String shaftNumbers;
    private double newWeight;
    private int newnumberOfBags;
    private int numberOfBags;
    private double weight;
    private String dedicationReason;
    private String transportStatus;
    private String selectedTransportdriver;
    private String selectedTransport;
    private List<Tax> tax;
    private List<Mill> mills;
    private List<GoldSale> goldSales;
    private String transportReason;
    private String securityDispatcherStatus;
    private String processStatus;
    private List<TransportCost> transportCosts;
    private List<OreSample> oreSample;
    private String location;
    private LocalDateTime date;
    private LocalDateTime time;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

}
