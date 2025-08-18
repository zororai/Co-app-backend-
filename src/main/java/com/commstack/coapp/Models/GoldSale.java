package com.commstack.coapp.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldSale {
    private String id;
    private double weight;
    private double price;
    private String buyer;

    // Getters and Setters
}
