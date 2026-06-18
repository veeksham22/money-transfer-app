package com.progressive.banking.moneytransfer.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a coupon redeemed by a user using their reward points.
 * Tracks the coupon code, discount percentage, merchant, and points spent.
 */
@Entity
@Table(
    name = "coupon_redemption",
    indexes = {
        @Index(name = "idx_coupon_username", columnList = "username"),
        @Index(name = "idx_coupon_code", columnList = "couponCode"),
        @Index(name = "idx_coupon_redeemed_at", columnList = "redeemedAt")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 10, unique = true)
    private String couponCode;

    @Column(nullable = false, length = 50)
    private String merchant;  // e.g., "Myntra", "Dominos", "La Pino's"

    @Column(nullable = false)
    private Integer discountPercentage;  // 5, 10, 15, etc.

    @Column(nullable = false)
    private Integer pointsSpent;  // 5, 10, 15, etc.

    @Column(nullable = false, length = 20)
    private String status;  // ACTIVE, EXPIRED, USED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime expiryDate;  // Coupon expiry (e.g., 30 days from redemption)

    @Column(nullable = false)
    @PrePersist
    private void setRedeemedAt() {
        if (redeemedAt == null) {
            redeemedAt = LocalDateTime.now();
        }
    }

    private LocalDateTime redeemedAt;  // When coupon was redeemed

    private LocalDateTime usedAt;  // When customer actually used it (optional tracking)

    /**
     * Check if coupon is still valid (not expired and status is ACTIVE)
     */
    public boolean isValid() {
        return status.equalsIgnoreCase("ACTIVE") && LocalDateTime.now().isBefore(expiryDate);
    }
}
