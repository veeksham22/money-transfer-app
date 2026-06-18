package com.progressive.banking.moneytransfer.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progressive.banking.moneytransfer.domain.dto.CouponRedemptionRequest;
import com.progressive.banking.moneytransfer.domain.dto.CouponRedemptionResponse;
import com.progressive.banking.moneytransfer.domain.entities.CouponRedemption;
import com.progressive.banking.moneytransfer.service.RedemptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/redemptions")
@RequiredArgsConstructor
public class RedemptionController {

    private final RedemptionService redemptionService;

    /**
     * Redeem points for a coupon
     * POST /api/v1/redemptions/redeem
     * Request: { pointsToSpend: 5, merchant: "Myntra" }
     * Response: { id, couponCode, merchant, discountPercentage, status, expiryDate, remainingPoints }
     */
    @PostMapping("/redeem")
    public ResponseEntity<CouponRedemptionResponse> redeemCoupon(
            @RequestBody CouponRedemptionRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        CouponRedemptionResponse response = redemptionService.redeemCoupon(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all redeemed coupons for logged-in user
     * GET /api/v1/redemptions/my-coupons
     */
    @GetMapping("/my-coupons")
    public ResponseEntity<List<CouponRedemption>> getUserCoupons(Authentication authentication) {
        String username = authentication.getName();
        List<CouponRedemption> coupons = redemptionService.getUserCoupons(username);
        return ResponseEntity.ok(coupons);
    }

    /**
     * Get active (not expired) coupons for logged-in user
     * GET /api/v1/redemptions/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<CouponRedemption>> getActiveCoupons(Authentication authentication) {
        String username = authentication.getName();
        List<CouponRedemption> activeCoupons = redemptionService.getActiveCoupons(username);
        return ResponseEntity.ok(activeCoupons);
    }

    /**
     * Get available points (after deducting redeemed points)
     * GET /api/v1/redemptions/available-points
     */
    @GetMapping("/available-points")
    public ResponseEntity<Long> getAvailablePoints(Authentication authentication) {
        String username = authentication.getName();
        long availablePoints = redemptionService.getAvailablePoints(username);
        return ResponseEntity.ok(availablePoints);
    }
}
