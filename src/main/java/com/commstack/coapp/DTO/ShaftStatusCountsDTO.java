package com.commstack.coapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShaftStatusCountsDTO {
    private long suspendedCount;
    private long approvedCount;
}
