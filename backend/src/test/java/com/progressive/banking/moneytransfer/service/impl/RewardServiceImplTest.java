package com.progressive.banking.moneytransfer.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.progressive.banking.moneytransfer.domain.entities.Account;
import com.progressive.banking.moneytransfer.domain.entities.RewardGrant;
import com.progressive.banking.moneytransfer.domain.entities.TransactionLog;
import com.progressive.banking.moneytransfer.domain.enums.AccountStatusEnum;
import com.progressive.banking.moneytransfer.domain.enums.TransactionStatusEnum;
import com.progressive.banking.moneytransfer.repository.RewardGrantRepository;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private RewardGrantRepository rewardGrantRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private Account account(int id, String holder) {
        Account a = new Account();
        a.setAccountId(id);
        a.setHolderName(holder);
        a.setBalance(BigDecimal.valueOf(5000));
        a.setStatus(AccountStatusEnum.ACTIVE);
        return a;
    }

    private TransactionLog successLog(int txnId, int from, int to, BigDecimal amount) {
        TransactionLog log = new TransactionLog();
        log.setTransactionId(txnId);
        log.setFromAccountId(from);
        log.setToAccountId(to);
        log.setAmount(amount);
        log.setStatus(TransactionStatusEnum.SUCCESS);
        log.setIdempotencyKey("key-" + txnId);
        log.setCreatedOn(LocalDateTime.now());
        return log;
    }

    @Test
    @DisplayName("calculatePoints: ₹250 gives 2 points")
    void calculatePoints_250_returns2() {
        assertEquals(2, rewardService.calculatePoints(BigDecimal.valueOf(250)));
    }

    @Test
    @DisplayName("calculatePoints: ₹199 gives 1 point")
    void calculatePoints_199_returns1() {
        assertEquals(1, rewardService.calculatePoints(BigDecimal.valueOf(199)));
    }

    @Test
    @DisplayName("calculatePoints: ₹99 gives 0 points")
    void calculatePoints_99_returns0() {
        assertEquals(0, rewardService.calculatePoints(BigDecimal.valueOf(99)));
    }

    @Test
    @DisplayName("isEligible returns false when amount is ₹100 or less")
    void isEligible_amountAtOrBelow100_returnsFalse() {
        Account from = account(1, "Alice");
        Account to = account(2, "Bob");
        TransactionLog log = successLog(1, 1, 2, BigDecimal.valueOf(100));

        assertFalse(rewardService.isEligible(log, from, to));
    }

    @Test
    @DisplayName("isEligible returns false for self-transfer (same holder)")
    void isEligible_sameHolder_returnsFalse() {
        Account from = account(1, "Alice");
        Account to = account(2, "Alice");
        TransactionLog log = successLog(1, 1, 2, BigDecimal.valueOf(250));

        assertFalse(rewardService.isEligible(log, from, to));
    }

    @Test
    @DisplayName("isEligible returns true for valid transfer above ₹100")
    void isEligible_validTransfer_returnsTrue() {
        Account from = account(1, "Alice");
        Account to = account(2, "Bob");
        TransactionLog log = successLog(1, 1, 2, BigDecimal.valueOf(250));

        assertTrue(rewardService.isEligible(log, from, to));
    }

    @Test
    @DisplayName("processTransferReward grants points for eligible transfer")
    void processTransferReward_eligible_savesGrant() {
        Account from = account(1, "Alice");
        Account to = account(2, "Bob");
        TransactionLog log = successLog(10, 1, 2, BigDecimal.valueOf(250));

        when(rewardGrantRepository.existsByTransactionId(10)).thenReturn(false);
        when(rewardGrantRepository.save(any(RewardGrant.class))).thenAnswer(inv -> {
            RewardGrant g = inv.getArgument(0);
            g.setId(1L);
            return g;
        });

        Optional<RewardGrant> result = rewardService.processTransferReward(log, from, to);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getPoints());
        assertEquals("Alice", result.get().getUsername());

        ArgumentCaptor<RewardGrant> captor = ArgumentCaptor.forClass(RewardGrant.class);
        verify(rewardGrantRepository).save(captor.capture());
        assertEquals(10, captor.getValue().getTransactionId());
    }

    @Test
    @DisplayName("processTransferReward skips ineligible transfer")
    void processTransferReward_ineligible_doesNotSave() {
        Account from = account(1, "Alice");
        Account to = account(2, "Bob");
        TransactionLog log = successLog(11, 1, 2, BigDecimal.valueOf(50));

        when(rewardGrantRepository.existsByTransactionId(11)).thenReturn(false);

        Optional<RewardGrant> result = rewardService.processTransferReward(log, from, to);

        assertFalse(result.isPresent());
        verify(rewardGrantRepository, never()).save(any());
    }

    @Test
    @DisplayName("processTransferReward is idempotent per transaction")
    void processTransferReward_duplicateTransaction_skipsSave() {
        Account from = account(1, "Alice");
        Account to = account(2, "Bob");
        TransactionLog log = successLog(12, 1, 2, BigDecimal.valueOf(500));
        RewardGrant existing = new RewardGrant();
        existing.setTransactionId(12);
        existing.setPoints(5);

        when(rewardGrantRepository.existsByTransactionId(12)).thenReturn(true);
        when(rewardGrantRepository.findByTransactionId(12)).thenReturn(Optional.of(existing));

        Optional<RewardGrant> result = rewardService.processTransferReward(log, from, to);

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getPoints());
        verify(rewardGrantRepository, never()).save(any());
    }
}
