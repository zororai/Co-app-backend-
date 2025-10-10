package com.commstack.coapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyTotalsDTO {
    private int year;
    private int month;
    private double totalWeight;
    private double totalNewWeight;
}
