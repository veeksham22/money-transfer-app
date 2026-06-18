package com.progressive.banking.moneytransfer.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.progressive.banking.moneytransfer.domain.dto.CouponRedemptionRequest;
import com.progressive.banking.moneytransfer.domain.dto.CouponRedemptionResponse;
import com.progressive.banking.moneytransfer.domain.entities.CouponRedemption;
import com.progressive.banking.moneytransfer.exception.InsufficientBalanceException;
import com.progressive.banking.moneytransfer.repository.CouponRedemptionRepository;
import com.progressive.banking.moneytransfer.repository.RewardGrantRepository;
import com.progressive.banking.moneytransfer.service.RedemptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedemptionServiceImpl implements RedemptionService {

    private final CouponRedemptionRepository couponRedemptionRepository;
    private final RewardGrantRepository rewardGrantRepository;

    // Coupon validity: 30 days from redemption
    private static final int COUPON_EXPIRY_DAYS = 30;

    // Merchant list
    private static final String[] VALID_MERCHANTS = {"Myntra", "Dominos", "La Pino's"};

    @Override
    @Transactional
    public CouponRedemptionResponse redeemCoupon(CouponRedemptionRequest request, String username) {

        // 1. Validate inputs
        if (!isValidPointValue(request.getPointsToSpend())) {
            throw new IllegalArgumentException(
                    "Invalid points value. Must be 5, 10, 15, or 20."
            );
        }

        if (!isValidMerchant(request.getMerchant())) {
            throw new IllegalArgumentException(
                    "Invalid merchant. Valid merchants: Myntra, Dominos, La Pino's"
            );
        }

        // 2. Check available points (total granted - already redeemed)
        long availablePoints = getAvailablePoints(username);
        if (availablePoints < request.getPointsToSpend()) {
            throw new InsufficientBalanceException(
                    "Insufficient reward points. Available: " + availablePoints +
                    ", Required: " + request.getPointsToSpend()
            );
        }

        // 3. Generate unique coupon code (6-character alphanumeric)
        String couponCode = generateUniqueCouponCode();

        // 4. Map points to discount percentage (1 point = 1% off)
        int discountPercentage = request.getPointsToSpend();

        // 5. Create coupon redemption entity
        CouponRedemption coupon = CouponRedemption.builder()
                .username(username)
                .couponCode(couponCode)
                .merchant(request.getMerchant())
                .discountPercentage(discountPercentage)
                .pointsSpent(request.getPointsToSpend())
                .status("ACTIVE")
                .redeemedAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(COUPON_EXPIRY_DAYS))
                .build();

        // 6. Save coupon (deduction of points is implicit through this record)
        CouponRedemption saved = couponRedemptionRepository.save(coupon);

        log.info("Coupon redeemed for user {}: code={}, merchant={}, discount={}%, points={}",
                username, couponCode, request.getMerchant(), discountPercentage, request.getPointsToSpend());

        // 7. Return response with remaining points
        long remainingPoints = getAvailablePoints(username);

        return CouponRedemptionResponse.builder()
                .id(saved.getId())
                .couponCode(saved.getCouponCode())
                .merchant(saved.getMerchant())
                .discountPercentage(saved.getDiscountPercentage())
                .pointsSpent(saved.getPointsSpent())
                .status(saved.getStatus())
                .redeemedAt(saved.getRedeemedAt())
                .expiryDate(saved.getExpiryDate())
                .remainingPoints((int) remainingPoints)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponRedemption> getUserCoupons(String username) {
        return couponRedemptionRepository.findByUsernameOrderByRedeemedAtDesc(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponRedemption> getActiveCoupons(String username) {
        return couponRedemptionRepository.findActiveCoupons(username);
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailablePoints(String username) {
        // Total points granted
        long totalPoints = rewardGrantRepository.sumPointsByUsername(username);

        // Total points redeemed (spent on coupons)
        long redeemedPoints = couponRedemptionRepository.findByUsernameOrderByRedeemedAtDesc(username)
                .stream()
                .filter(c -> c.getStatus().equalsIgnoreCase("ACTIVE") || c.getStatus().equalsIgnoreCase("USED"))
                .mapToLong(CouponRedemption::getPointsSpent)
                .sum();

        return totalPoints - redeemedPoints;
    }

    @Override
    @Transactional
    public void markCouponAsUsed(String couponCode) {
        CouponRedemption coupon = couponRedemptionRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found: " + couponCode));

        if (!coupon.getStatus().equalsIgnoreCase("ACTIVE")) {
            throw new IllegalArgumentException("Coupon is not active: " + couponCode);
        }

        coupon.setStatus("USED");
        coupon.setUsedAt(LocalDateTime.now());
        couponRedemptionRepository.save(coupon);

        log.info("Coupon marked as used: {} by user {}", couponCode, coupon.getUsername());
    }

    /**
     * Generate a unique 6-character alphanumeric coupon code
     */
    private String generateUniqueCouponCode() {
        String code;
        do {
            // Generate random 6-char code (uppercase + digits)
            code = UUID.randomUUID()
                    .toString()
                    .replaceAll("-", "")
                    .substring(0, 6)
                    .toUpperCase();
        } while (couponRedemptionRepository.existsByCouponCode(code));

        return code;
    }

    /**
     * Validate points value (must be multiple of 5)
     */
    private boolean isValidPointValue(Integer points) {
        return points != null && points >= 5 && points % 5 == 0 && points <= 100;
    }

    /**
     * Validate merchant name
     */
    private boolean isValidMerchant(String merchant) {
        for (String valid : VALID_MERCHANTS) {
            if (valid.equalsIgnoreCase(merchant)) {
                return true;
            }
        }
        return false;
    }
}
