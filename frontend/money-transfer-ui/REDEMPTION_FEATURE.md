# Reward Redemption System - Feature Documentation

## Overview

The Reward Redemption System allows users to redeem their accumulated reward points for coupon codes at partner merchants. Users earn 1 point for every ₹100 transferred (over ₹100 minimum), and can then redeem these points for discount coupons.

---

## Features Implemented

### 1. **Coupon Redemption**
- Browse available coupons based on current reward points
- 5-point tier: 5% off coupons
- 10-point tier: 10% off coupons
- Partner merchants: Myntra, Domino's, La Pino's Pizza, McDonald's

### 2. **Coupon Generation**
- Random 6-digit alphanumeric coupon codes (e.g., `K7X2M9`)
- Generated upon redemption
- Unique for each redemption

### 3. **Redemption History**
- Track all redeemed coupons
- View coupon details (code, partner, discount, points cost)
- Mark coupons as used
- Track expiry dates (30 days validity)
- Copy coupon codes to clipboard

### 4. **Frontend-Only Implementation**
- No backend changes required initially
- Uses localStorage for persistence
- Can be extended to backend in future phases

---

## Architecture

### Component Structure

```
RedemptionComponent (New)
├── Features
│   └── redemption/
│       ├── redemption.component.ts
│       ├── redemption.component.html
│       └── redemption.component.scss
├── Core Services
│   └── services/
│       └── redemption.service.ts
└── Core Models
    └── models/
        └── coupon.model.ts
```

### File Locations

```
Frontend Structure:
├── src/app/
│   ├── features/
│   │   ├── redemption/
│   │   │   ├── redemption.component.ts
│   │   │   ├── redemption.component.html
│   │   │   └── redemption.component.scss
│   │   ├── rewards/
│   │   │   ├── rewards.component.ts
│   │   │   ├── rewards.component.html
│   │   │   └── rewards.component.scss (UPDATED)
│   │   └── dashboard/
│   │       ├── dashboard.component.ts (UPDATED)
│   │       ├── dashboard.component.html (UPDATED)
│   │       └── dashboard.component.scss (UPDATED)
│   ├── core/
│   │   ├── services/
│   │   │   ├── redemption.service.ts (NEW)
│   │   │   └── ...
│   │   └── models/
│   │       ├── coupon.model.ts (NEW)
│   │       └── ...
│   └── app.routes.ts (UPDATED)
```

---

## Data Models

### CouponOffer Model

```typescript
interface CouponOffer {
  id: string;                      // Unique identifier (e.g., 'myntra-5')
  partner: string;                 // 'Myntra' | 'Dominos' | 'La Pino\'s Pizza' | 'McDonald\'s'
  discountPercentage: number;      // 5 or 10
  pointsRequired: number;          // 5 or 10
  description: string;             // User-friendly description
  validity: string;                // e.g., "30 days"
  terms: string[];                 // Terms & conditions array
}
```

### RedeemedCoupon Model

```typescript
interface RedeemedCoupon {
  id: string;                      // Unique redemption ID
  couponCode: string;              // Generated 6-char code (e.g., 'K7X2M9')
  partner: string;                 // Partner name
  discountPercentage: number;      // Discount % (5 or 10)
  pointsRedeemed: number;          // Points spent
  redeemedAt: string;              // ISO timestamp when redeemed
  expiresAt: string;               // ISO timestamp when expires (30 days)
  isUsed: boolean;                 // Whether coupon was marked as used
  usedAt?: string;                 // ISO timestamp when marked used
}
```

### RedemptionHistory Model

```typescript
interface RedemptionHistory {
  totalCouponsRedeemed: number;    // Count of all redeemed coupons
  availableCoupons: RedeemedCoupon[]; // Not used, not expired
  usedCoupons: RedeemedCoupon[];      // Marked as used or expired
}
```

---

## RedemptionService API

### Key Methods

#### `getAvailableCoupons(currentPoints: number): CouponOffer[]`
Returns list of coupons user can afford with their current points.

```typescript
// User has 12 points
const coupons = redemptionService.getAvailableCoupons(12);
// Returns: 5% offers (5pts) + 10% offers (10pts)
```

#### `redeemCoupon(offer: CouponOffer, currentPoints: number): RedeemedCoupon | null`
Redeems points for a coupon. Generates code and returns redeemed coupon.

```typescript
const offer: CouponOffer = {
  id: 'myntra-5',
  partner: 'Myntra',
  discountPercentage: 5,
  pointsRequired: 5,
  // ...
};

const redeemed = redemptionService.redeemCoupon(offer, 12);
// Returns: {
//   id: 'coupon-1718...',
//   couponCode: 'K7X2M9',      // Generated
//   partner: 'Myntra',
//   discountPercentage: 5,
//   pointsRedeemed: 5,
//   redeemedAt: '2026-06-18T...',
//   expiresAt: '2026-07-18T...',
//   isUsed: false
// }
```

#### `getRedeemedCoupons(): Observable<RedeemedCoupon[]>`
Observable stream of all redeemed coupons.

```typescript
redemptionService.getRedeemedCoupons().subscribe(coupons => {
  console.log('All redeemed coupons:', coupons);
});
```

#### `markCouponAsUsed(couponId: string): void`
Marks a coupon as used with timestamp.

```typescript
redemptionService.markCouponAsUsed('coupon-1718...');
```

#### `isCouponExpired(coupon: RedeemedCoupon): boolean`
Checks if coupon has expired.

```typescript
if (redemptionService.isCouponExpired(coupon)) {
  console.log('Coupon expired!');
}
```

#### `getActiveCoupons(): RedeemedCoupon[]`
Returns only non-expired, unused coupons.

```typescript
const active = redemptionService.getActiveCoupons();
```

---

## RedemptionComponent Features

### Tab Navigation
- **Available Coupons Tab**: Shows coupons user can redeem based on points
- **My Coupons Tab**: Shows all redeemed coupons with status tracking

### Available Coupons Display
```
Card for each coupon showing:
├── Partner icon & name
├── Discount percentage badge
├── Description
├── Points required & validity
├── Redeem button (disabled if insufficient points)
└── Expandable details (terms & conditions)
```

### Redeemed Coupons Display
```
List showing each redeemed coupon:
├── Status badge (Active, Used, Expired)
├── Partner & discount info
├── Coupon code display with copy button
├── Points spent, redemption date, expiry date
├── Days remaining (warning if < 5 days)
└── "Mark as Used" button (for active coupons)
```

### Confirmation Modal
```
Shows before redemption:
├── Partner details
├── Discount & cost
├── Points remaining after redemption
├── Warning message
└── Cancel/Confirm buttons
```

---

## State Management

### Using BehaviorSubject in Service

```typescript
private redeemedCouponsSubject = new BehaviorSubject<RedeemedCoupon[]>(
  this.loadFromStorage()
);

// Component receives updates via Observable
this.redemptionService.getRedeemedCoupons().subscribe(coupons => {
  this.redeemedCoupons.set(coupons);
});
```

### Local Storage Persistence

```
Storage Key: 'mts_redeemed_coupons'
Storage Value: JSON array of RedeemedCoupon objects

Example:
[
  {
    "id": "coupon-1718700000000-abc123",
    "couponCode": "K7X2M9",
    "partner": "Myntra",
    "discountPercentage": 5,
    "pointsRedeemed": 5,
    "redeemedAt": "2026-06-18T10:30:00.000Z",
    "expiresAt": "2026-07-18T10:30:00.000Z",
    "isUsed": false
  },
  // ...
]
```

---

## User Flows

### Flow 1: Browsing Available Coupons

```
User navigates to /redeem
    ↓
RedemptionComponent loads
    ↓
1. Fetch current points from RewardsService
2. Call getAvailableCoupons(currentPoints)
    ↓
Display available coupons in grid
    ↓
User can:
├─ View coupon details (click to expand)
├─ See partner info & discount
├─ Read terms & conditions
└─ Redeem (if sufficient points)
```

### Flow 2: Redeeming a Coupon

```
User clicks "Redeem Now"
    ↓
Show confirmation modal with:
├─ Partner details
├─ Discount amount
├─ Points to spend
└─ Remaining points after
    ↓
User confirms (or cancels)
    ↓
redeemCoupon() called:
1. Validate points available
2. Generate 6-char coupon code
3. Calculate 30-day expiry date
4. Create RedeemedCoupon object
5. Save to localStorage
6. Update BehaviorSubject
7. Show success toast
    ↓
Update UI:
├─ Points badge decreases
├─ Available coupons refreshed
└─ Switch to "My Coupons" tab
```

### Flow 3: Using a Coupon

```
User receives redeemed coupon
    ↓
User goes to partner's store/app
    ↓
User enters coupon code (e.g., K7X2M9)
    ↓
Gets discount (5% or 10%)
    ↓
Returns to app and clicks "Mark as Used"
    ↓
markCouponAsUsed(couponId):
1. Find coupon by ID
2. Set isUsed = true
3. Set usedAt = current timestamp
4. Save to localStorage
5. Update BehaviorSubject
    ↓
UI updates coupon status to "✓ Used"
```

---

## Coupon Offers Configuration

### Available Partners & Tiers

| Partner | 5% Offer | 10% Offer |
|---------|----------|-----------|
| Myntra | 5 pts, Min ₹500 | 10 pts, Min ₹1,000 |
| Domino's | 5 pts, Min ₹300 | 10 pts, Min ₹600 |
| La Pino's Pizza | 5 pts, Min ₹350 | 10 pts, Min ₹700 |
| McDonald's | 5 pts, Min ₹250 | 10 pts, Min ₹500 |

### Terms for Each Offer

```
Myntra:
- Valid on minimum purchase of ₹500 (5%) / ₹1,000 (10%)
- Cannot be combined with other offers
- One coupon per transaction

Domino's:
- Valid on minimum order value of ₹300 (5%) / ₹600 (10%)
- Applicable on online orders only
- One coupon per order

La Pino's Pizza:
- Valid on minimum order value of ₹350 (5%) / ₹700 (10%)
- Not applicable on delivery charges
- One coupon per order

McDonald's:
- Valid on minimum purchase of ₹250 (5%) / ₹500 (10%)
- Applicable on in-store purchases only
- One coupon per transaction
```

---

## UI/UX Features

### 1. **Responsive Design**
- Desktop: Grid layout with multiple cards
- Tablet: 2-column grid
- Mobile: Single column, full width

### 2. **Visual Feedback**
- Loading spinners
- Success/error toast notifications
- Status badges (Active, Used, Expired)
- Disabled button states
- Hover effects & animations

### 3. **Accessibility**
- Semantic HTML structure
- ARIA labels on interactive elements
- Keyboard navigation support
- Color contrast compliant

### 4. **Performance**
- Lazy loading component
- Efficient localStorage usage
- Minimal re-renders (signals + OnPush strategy possible)
- No unnecessary API calls

---

## Integration Points

### 1. **Navigation Links**
```typescript
// Dashboard component
<button (click)="goToRedemption()">🎁 Redeem ({{ points }})</button>

// Rewards component
<button (click)="navigateToRedemption()">🎁 Redeem Rewards</button>

// Route
{ path: 'redeem', component: RedemptionComponent, canActivate: [authGuard] }
```

### 2. **Points Display**
```typescript
// Dashboard
userPoints$ = this.rewardsService.getSummary().pipe(
  map(summary => summary.totalPoints)
)

// Displayed in button: 🎁 Redeem (12)
```

### 3. **Auth Guard**
All redemption routes require JWT authentication via `authGuard`.

---

## Backend Integration (Future)

When extending to backend:

### 1. **Coupon API Endpoints**
```
GET /api/v1/coupons/available?points={points}
GET /api/v1/coupons/redeemed
POST /api/v1/coupons/redeem
  { offerId: string }
  Returns: { couponCode, partner, validUntil }
```

### 2. **Data Persistence**
- Move coupons from localStorage to database
- Track redemptions in `coupon_redemptions` table
- Link to accounts/users

### 3. **Backend Service**
```java
@Service
public class CouponRedemptionService {
  public CouponCode redeemCoupon(String username, CouponOfferId offerId) {
    // Validate points
    // Generate code
    // Save to DB
    // Deduct points (or not, depending on business logic)
  }
}
```

---

## Testing Strategy

### Unit Tests
```typescript
// redemption.service.spec.ts
describe('RedemptionService', () => {
  it('should generate valid coupon code', () => {
    const code = service['generateCouponCode']();
    expect(code).toMatch(/^[A-Z0-9]{6}$/);
  });

  it('should redeem coupon when sufficient points', () => {
    const result = service.redeemCoupon(offer, 10);
    expect(result).not.toBeNull();
    expect(result?.pointsRedeemed).toBe(5);
  });

  it('should return null when insufficient points', () => {
    const result = service.redeemCoupon(offer, 3);
    expect(result).toBeNull();
  });
});
```

### Component Tests
```typescript
// redemption.component.spec.ts
describe('RedemptionComponent', () => {
  it('should display available coupons', () => {
    // ...
  });

  it('should disable redeem button when insufficient points', () => {
    // ...
  });

  it('should show confirmation modal before redeeming', () => {
    // ...
  });
});
```

---

## Demo Scenarios

### Scenario 1: User with 5 Points
- Can redeem 1x 5% coupon
- Cannot redeem 10% coupons
- Available partners shown: All partners with 5% offers
- Click "Redeem Now" → Confirm → Get coupon code

### Scenario 2: User with 15 Points
- Can redeem multiple 5% or 10% coupons
- Can combine redemptions
- Example: 2x 5% offers (10 pts) + 1x 10% offer (10 pts) = not enough, but...
- Or: 3x 5% offers (15 pts) = exactly enough

### Scenario 3: Expired Coupon
- 30-day coupon after expiry date
- Status shows "✕ Expired"
- Cannot be used
- Faded out styling

### Scenario 4: Active vs Used Coupons
- Showing mix of active and used coupons
- Active: Green status badge, can mark as used
- Used: Blue status badge, marked with ✓
- Expired: Red status badge, marked with ✕

---

## Troubleshooting

### Issue: Coupons not persisting after page refresh
**Solution:** Check browser's localStorage:
- DevTools → Application → Local Storage
- Verify `mts_redeemed_coupons` key exists with valid JSON

### Issue: Can't redeem despite having points
**Solution:** Check:
1. Points loaded correctly from RewardsService
2. CouponOffer.pointsRequired matches available amount
3. No errors in browser console

### Issue: Coupon code not copying to clipboard
**Solution:** 
- Check HTTPS requirement (clipboard API requires secure context)
- Try fallback: Manual copy of displayed code

---

## Deployment Checklist

```
[ ] Test all coupon offers in each partner category
[ ] Verify localStorage quota (usually 5-10MB)
[ ] Test expiry date calculations
[ ] Verify responsive design on mobile
[ ] Check accessibility (WCAG 2.1 AA)
[ ] Load test with large coupon history (100+ coupons)
[ ] Clear cache & test first-load experience
[ ] Test logout clears coupons (or persists per user)
[ ] Verify error messages are user-friendly
[ ] Test with auth guard redirects
```

---

## Future Enhancements

1. **QR Code Generation**: Generate QR codes for easy scanning at partners
2. **Partner API Integration**: Real-time coupon availability from partner systems
3. **Redemption Analytics**: Track which coupons are most redeemed
4. **Tiered Redemption**: More offer tiers (3-point, 7-point, etc.)
5. **Referral Bonuses**: Extra points for referrals
6. **Point Expiry**: Auto-expire unused points after X days
7. **SMS/Email Coupons**: Send coupon codes via SMS/email instead of just app
8. **Partner Ratings**: User reviews for redeemed partners
9. **Seasonal Offers**: Limited-time special offers
10. **B2B Integration**: Corporate coupon management

---

## Support & Documentation

For questions or issues:
1. Check component comments in source code
2. Review service method JSDoc comments
3. Check Q&A_QUICK_REFERENCE.md for common questions
4. Review DEMO_SPEAKING_POINTS.md for architecture context

---

**Last Updated:** June 18, 2026  
**Version:** 1.0  
**Status:** Complete for frontend-only phase
