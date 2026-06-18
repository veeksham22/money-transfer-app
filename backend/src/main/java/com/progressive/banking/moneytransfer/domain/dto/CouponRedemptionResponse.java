package com.progressive.banking.moneytransfer.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedemptionResponse {
    private Long id;
    private String couponCode;
    private String merchant;
    private Integer discountPercentage;
    private Integer pointsSpent;
    private String status;
    private LocalDateTime redeemedAt;
    private LocalDateTime expiryDate;
    private Integer remainingPoints;  // After redemption
}
