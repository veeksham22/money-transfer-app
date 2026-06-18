package com.progressive.banking.moneytransfer.service;

import java.util.List;

import com.progressive.banking.moneytransfer.domain.dto.CouponRedemptionRequest;
import com.progressive.banking.moneytransfer.domain.dto.CouponRedemptionResponse;
import com.progressive.banking.moneytransfer.domain.entities.CouponRedemption;

/**
 * Service for handling coupon redemptions using reward points
 */
public interface RedemptionService {

    /**
     * Redeem points for a coupon
     * - Validates user has enough points
     * - Deducts points from reward balance
     * - Generates unique coupon code
     * - Returns coupon details
     * - Idempotent: Same request returns existing coupon or new one
     */
    CouponRedemptionResponse redeemCoupon(CouponRedemptionRequest request, String username);

    /**
     * Get all redeemed coupons for a user
     */
    List<CouponRedemption> getUserCoupons(String username);

    /**
     * Get active (not expired, not used) coupons for a user
     */
    List<CouponRedemption> getActiveCoupons(String username);

    /**
     * Get total points available for a user (after deducting redeemed points)
     */
    long getAvailablePoints(String username);

    /**
     * Mark coupon as used (when customer applies it at merchant)
     */
    void markCouponAsUsed(String couponCode);
}
