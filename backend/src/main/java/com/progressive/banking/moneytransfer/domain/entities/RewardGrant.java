package com.progressive.banking.moneytransfer.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "reward_grant",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_reward_transaction", columnNames = "transactionId")
    },
    indexes = {
        @Index(name = "idx_reward_username", columnList = "username"),
        @Index(name = "idx_reward_granted_at", columnList = "grantedAt")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private Integer transactionId;

    @Column(nullable = false)
    private Integer fromAccountId;

    @Column(nullable = false)
    private Integer toAccountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal transactionAmount;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false, updatable = false)
    private LocalDateTime grantedAt;

    @PrePersist
    void onCreate() {
        if (grantedAt == null) {
            grantedAt = LocalDateTime.now();
        }
    }
}
