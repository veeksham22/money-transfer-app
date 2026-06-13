package com.progressive.banking.moneytransfer.service;

import java.util.List;
import java.util.Optional;

import com.progressive.banking.moneytransfer.domain.dto.RewardHistoryResponse;
import com.progressive.banking.moneytransfer.domain.dto.RewardSummaryResponse;
import com.progressive.banking.moneytransfer.domain.entities.Account;
import com.progressive.banking.moneytransfer.domain.entities.RewardGrant;
import com.progressive.banking.moneytransfer.domain.entities.TransactionLog;

public interface RewardService {

    /**
     * Evaluates eligibility and grants reward points to the sender for a successful transfer.
     * Idempotent per transaction — duplicate calls return empty.
     */
    Optional<RewardGrant> processTransferReward(TransactionLog transaction, Account from, Account to);

    RewardSummaryResponse getSummary(String username);

    List<RewardHistoryResponse> getHistory(String username);
}
