export interface CouponOffer {
  id: string;
  partner: 'Myntra' | 'Dominos' | 'La Pino\'s Pizza' | 'McDonald\'s';
  discountPercentage: number;
  pointsRequired: number;
  description: string;
  validity: string; // e.g., "30 days"
  terms: string[];
}

export interface RedeemedCoupon {
  id: string;
  couponCode: string;
  partner: string;
  discountPercentage: number;
  pointsRedeemed: number;
  redeemedAt: string;
  expiresAt: string;
  isUsed: boolean;
  usedAt?: string;
}

export interface RedemptionHistory {
  totalCouponsRedeemed: number;
  availableCoupons: RedeemedCoupon[];
  usedCoupons: RedeemedCoupon[];
}

export const COUPON_OFFERS: CouponOffer[] = [
  {
    id: 'myntra-5',
    partner: 'Myntra',
    discountPercentage: 5,
    pointsRequired: 5,
    description: '5% off on all products at Myntra',
    validity: '30 days',
    terms: [
      'Valid on minimum purchase of ₹500',
      'Cannot be combined with other offers',
      'One coupon per transaction'
    ]
  },
  {
    id: 'myntra-10',
    partner: 'Myntra',
    discountPercentage: 10,
    pointsRequired: 10,
    description: '10% off on all products at Myntra',
    validity: '30 days',
    terms: [
      'Valid on minimum purchase of ₹1,000',
      'Cannot be combined with other offers',
      'One coupon per transaction'
    ]
  },
  {
    id: 'dominos-5',
    partner: 'Dominos',
    discountPercentage: 5,
    pointsRequired: 5,
    description: '5% off on all orders at Domino\'s Pizza',
    validity: '30 days',
    terms: [
      'Valid on minimum order value of ₹300',
      'Applicable on online orders only',
      'One coupon per order'
    ]
  },
  {
    id: 'dominos-10',
    partner: 'Dominos',
    discountPercentage: 10,
    pointsRequired: 10,
    description: '10% off on all orders at Domino\'s Pizza',
    validity: '30 days',
    terms: [
      'Valid on minimum order value of ₹600',
      'Applicable on online orders only',
      'One coupon per order'
    ]
  },
  {
    id: 'lapinos-5',
    partner: 'La Pino\'s Pizza',
    discountPercentage: 5,
    pointsRequired: 5,
    description: '5% off on all orders at La Pino\'s Pizza',
    validity: '30 days',
    terms: [
      'Valid on minimum order value of ₹350',
      'Not applicable on delivery charges',
      'One coupon per order'
    ]
  },
  {
    id: 'lapinos-10',
    partner: 'La Pino\'s Pizza',
    discountPercentage: 10,
    pointsRequired: 10,
    description: '10% off on all orders at La Pino\'s Pizza',
    validity: '30 days',
    terms: [
      'Valid on minimum order value of ₹700',
      'Not applicable on delivery charges',
      'One coupon per order'
    ]
  },
  {
    id: 'mcdonalds-5',
    partner: 'McDonald\'s',
    discountPercentage: 5,
    pointsRequired: 5,
    description: '5% off on all items at McDonald\'s',
    validity: '30 days',
    terms: [
      'Valid on minimum purchase of ₹250',
      'Applicable on in-store purchases only',
      'One coupon per transaction'
    ]
  },
  {
    id: 'mcdonalds-10',
    partner: 'McDonald\'s',
    discountPercentage: 10,
    pointsRequired: 10,
    description: '10% off on all items at McDonald\'s',
    validity: '30 days',
    terms: [
      'Valid on minimum purchase of ₹500',
      'Applicable on in-store purchases only',
      'One coupon per transaction'
    ]
  }
];
