package com.progressive.banking.moneytransfer.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardHistoryResponse {

    private Long rewardId;
    private Integer transactionId;
    private Integer fromAccountId;
    private Integer toAccountId;
    private BigDecimal transactionAmount;
    private int points;
    private LocalDateTime grantedAt;
}
