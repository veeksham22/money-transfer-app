package com.progressive.banking.moneytransfer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.progressive.banking.moneytransfer.domain.entities.CouponRedemption;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    /**
     * Find coupon by code
     */
    Optional<CouponRedemption> findByCouponCode(String couponCode);

    /**
     * Find all coupons redeemed by a user, ordered by most recent first
     */
    List<CouponRedemption> findByUsernameOrderByRedeemedAtDesc(String username);

    /**
     * Find active coupons for a user (not expired, status = ACTIVE)
     */
    @Query("SELECT c FROM CouponRedemption c WHERE c.username = :username " +
           "AND c.status = 'ACTIVE' AND c.expiryDate > CURRENT_TIMESTAMP " +
           "ORDER BY c.redeemedAt DESC")
    List<CouponRedemption> findActiveCoupons(@Param("username") String username);

    /**
     * Count total coupons redeemed by user
     */
    long countByUsername(String username);

    /**
     * Check if a coupon code already exists
     */
    boolean existsByCouponCode(String couponCode);
}
