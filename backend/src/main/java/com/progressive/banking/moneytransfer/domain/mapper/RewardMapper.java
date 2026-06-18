package com.progressive.banking.moneytransfer.domain.mapper;

import com.progressive.banking.moneytransfer.domain.dto.RewardHistoryResponse;
import com.progressive.banking.moneytransfer.domain.entities.RewardGrant;

public final class RewardMapper {

    private RewardMapper() {}

    public static RewardHistoryResponse toHistoryResponse(RewardGrant grant) {
        return RewardHistoryResponse.builder()
                .rewardId(grant.getId())
                .transactionId(grant.getTransactionId())
                .fromAccountId(grant.getFromAccountId())
                .toAccountId(grant.getToAccountId())
                .transactionAmount(grant.getTransactionAmount())
                .points(grant.getPoints())
                .grantedAt(grant.getGrantedAt())
                .build();
    }
}
