package com.progressive.banking.moneytransfer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardSummaryResponse {

    private String username;
    private long totalPoints;
    private long totalGrants;
    private int minEligibleAmount;
    private int pointsPerHundredRupees;
}
