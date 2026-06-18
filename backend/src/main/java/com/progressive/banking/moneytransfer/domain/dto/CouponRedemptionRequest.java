package com.progressive.banking.moneytransfer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedemptionRequest {
    private Integer pointsToSpend;  // 5, 10, 15, etc.
    private String merchant;  // "Myntra", "Dominos", "La Pino's"
}
