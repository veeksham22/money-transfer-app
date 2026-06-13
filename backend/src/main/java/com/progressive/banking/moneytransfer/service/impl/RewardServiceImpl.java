package com.progressive.banking.moneytransfer.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progressive.banking.moneytransfer.domain.RewardConstants;
import com.progressive.banking.moneytransfer.domain.dto.RewardHistoryResponse;
import com.progressive.banking.moneytransfer.domain.dto.RewardSummaryResponse;
import com.progressive.banking.moneytransfer.domain.entities.Account;
import com.progressive.banking.moneytransfer.domain.entities.RewardGrant;
import com.progressive.banking.moneytransfer.domain.entities.TransactionLog;
import com.progressive.banking.moneytransfer.domain.mapper.RewardMapper;
import com.progressive.banking.moneytransfer.repository.RewardGrantRepository;
import com.progressive.banking.moneytransfer.service.RewardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImpl implements RewardService {

    private final RewardGrantRepository rewardGrantRepository;

    @Override
    @Transactional
    public Optional<RewardGrant> processTransferReward(TransactionLog transaction, Account from, Account to) {
        if (rewardGrantRepository.existsByTransactionId(transaction.getTransactionId())) {
            log.debug("Reward already granted for transaction {}", transaction.getTransactionId());
            return rewardGrantRepository.findByTransactionId(transaction.getTransactionId());
        }

        if (!isEligible(transaction, from, to)) {
            log.debug("Transaction {} not eligible for rewards", transaction.getTransactionId());
            return Optional.empty();
        }

        int points = calculatePoints(transaction.getAmount());
        if (points <= 0) {
            return Optional.empty();
        }

        RewardGrant grant = new RewardGrant();
        grant.setUsername(from.getHolderName());
        grant.setTransactionId(transaction.getTransactionId());
        grant.setFromAccountId(transaction.getFromAccountId());
        grant.setToAccountId(transaction.getToAccountId());
        grant.setTransactionAmount(transaction.getAmount());
        grant.setPoints(points);

        try {
            RewardGrant saved = rewardGrantRepository.save(grant);
            log.info("Granted {} reward points to {} for transaction {}",
                    points, from.getHolderName(), transaction.getTransactionId());
            return Optional.of(saved);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Concurrent reward grant for transaction {} — treating as idempotent",
                    transaction.getTransactionId());
            return rewardGrantRepository.findByTransactionId(transaction.getTransactionId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RewardSummaryResponse getSummary(String username) {
        long totalPoints = rewardGrantRepository.sumPointsByUsername(username);
        long totalGrants = rewardGrantRepository.countByUsername(username);

        return RewardSummaryResponse.builder()
                .username(username)
                .totalPoints(totalPoints)
                .totalGrants(totalGrants)
                .minEligibleAmount(RewardConstants.MIN_ELIGIBLE_AMOUNT.intValue())
                .pointsPerHundredRupees(1)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardHistoryResponse> getHistory(String username) {
        return rewardGrantRepository.findByUsernameOrderByGrantedAtDesc(username).stream()
                .map(RewardMapper::toHistoryResponse)
                .toList();
    }

    /**
     * Eligibility: SUCCESS status, amount &gt; ₹100, different accounts, different users (not self-transfer).
     */
    boolean isEligible(TransactionLog transaction, Account from, Account to) {
        if (!transaction.isSuccess()) {
            return false;
        }
        if (transaction.getAmount() == null
                || transaction.getAmount().compareTo(RewardConstants.MIN_ELIGIBLE_AMOUNT) <= 0) {
            return false;
        }
        if (from.getAccountId().equals(to.getAccountId())) {
            return false;
        }
        if (from.getHolderName().equalsIgnoreCase(to.getHolderName())) {
            return false;
        }
        return true;
    }

    /** 1 point per ₹100 transferred, rounded down. */
    int calculatePoints(BigDecimal amount) {
        return amount.divide(RewardConstants.RUPEES_PER_POINT, 0, RoundingMode.DOWN).intValue();
    }
}
