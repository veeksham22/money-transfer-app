package com.progressive.banking.moneytransfer.domain;

import java.math.BigDecimal;

public final class RewardConstants {

    private RewardConstants() {}

    /** Minimum transfer amount (exclusive) for reward eligibility — must be greater than ₹100. */
    public static final BigDecimal MIN_ELIGIBLE_AMOUNT = new BigDecimal("100");

    /** Rupees per reward point (1 point per ₹100 transferred). */
    public static final BigDecimal RUPEES_PER_POINT = new BigDecimal("100");
}
