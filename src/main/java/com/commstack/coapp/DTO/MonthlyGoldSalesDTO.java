package com.commstack.coapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyGoldSalesDTO {
    private int year;
    private int month;
    private double totalWeight;
    private double totalPrice;
}
