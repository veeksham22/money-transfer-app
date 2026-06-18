import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { RewardsService } from '../../core/services/rewards.service';
import { RedemptionService } from '../../core/services/redemption.service';
import { CouponOffer, RedeemedCoupon } from '../../core/models/coupon.model';

@Component({
  selector: 'app-redemption',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './redemption.component.html',
  styleUrl: './redemption.component.scss'
})
export class RedemptionComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly rewardsService = inject(RewardsService);
  private readonly redemptionService = inject(RedemptionService);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  currentPoints = signal<number>(0);
  availableCoupons = signal<CouponOffer[]>([]);
  redeemedCoupons = signal<RedeemedCoupon[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  activeTab = signal<'available' | 'redeemed'>('available');
  expandedCouponId = signal<string | null>(null);
  showRedemptionConfirm = signal<CouponOffer | null>(null);

  ngOnInit(): void {
    if (!this.auth.getAccountId()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading.set(true);
    this.loadRewards();
  }

  private loadRewards(): void {
    this.redemptionService.getAvailablePointsFromBackend().subscribe({
      next: (availablePoints) => {
        this.currentPoints.set(availablePoints);
        this.loadAvailableCoupons();
        this.loadRedeemedCoupons();
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Unable to load rewards.');
        this.loading.set(false);
      }
    });
  }

  private loadAvailableCoupons(): void {
    const coupons = this.redemptionService.getAvailableCoupons(this.currentPoints());
    this.availableCoupons.set(coupons);
  }

  private loadRedeemedCoupons(): void {
    this.redemptionService.getUserCoupons().subscribe({
      next: (coupons) => {
        this.redeemedCoupons.set(coupons);
      }
    });
  }

  /**
   * Toggle coupon details view
   */
  toggleCouponDetails(couponId: string): void {
    if (this.expandedCouponId() === couponId) {
      this.expandedCouponId.set(null);
    } else {
      this.expandedCouponId.set(couponId);
    }
  }

  /**
   * Show confirmation dialog before redemption
   */
  confirmRedemption(coupon: CouponOffer): void {
    if (this.currentPoints() < coupon.pointsRequired) {
      this.error.set(`Insufficient points. You need ${coupon.pointsRequired} points but have ${this.currentPoints()}`);
      return;
    }
    this.showRedemptionConfirm.set(coupon);
  }

  /**
   * Actually redeem the coupon via backend API
   */
  redeemCoupon(coupon: CouponOffer): void {
    this.loading.set(true);
    
    // Get the actual merchant name from the partner field
    const merchantName = this.mapPartnerToMerchant(coupon.partner);
    
    this.redemptionService.redeemCoupon(coupon.pointsRequired, merchantName).subscribe({
      next: (response) => {
        this.successMessage.set(
          `🎉 Coupon redeemed successfully!\n\nCode: ${response.couponCode}\nDiscount: ${response.discountPercentage}%\nExpiry: ${response.expiryDate}`
        );
        
        // Update points (remaining after redemption)
        this.currentPoints.set(response.remainingPoints);
        
        // Reload coupons to reflect changes
        this.loadAvailableCoupons();
        this.loadRedeemedCoupons();
        
        // Clear confirmation dialog
        this.showRedemptionConfirm.set(null);
        
        // Clear success message after 7 seconds
        setTimeout(() => this.successMessage.set(null), 7000);
        
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Failed to redeem coupon. Please try again.');
        this.showRedemptionConfirm.set(null);
        this.loading.set(false);
      }
    });
  }

  /**
   * Map UI partner name to backend merchant name
   */
  private mapPartnerToMerchant(partner: string): string {
    const mapping: { [key: string]: string } = {
      'Myntra': 'Myntra',
      'Dominos': 'Dominos',
      'La Pino\'s Pizza': 'La Pino\'s',
      'McDonald\'s': 'McDonald\'s'
    };
    return mapping[partner] || partner;
  }

  /**
   * Cancel redemption
   */
  cancelRedemption(): void {
    this.showRedemptionConfirm.set(null);
    this.error.set(null);
  }

  /**
   * Copy coupon code to clipboard
   */
  copyCouponCode(code: string): void {
    navigator.clipboard.writeText(code).then(() => {
      this.successMessage.set('Coupon code copied to clipboard!');
      setTimeout(() => this.successMessage.set(null), 3000);
    });
  }

  /**
   * Mark coupon as used
   */
  markAsUsed(coupon: RedeemedCoupon): void {
    this.redemptionService.markCouponAsUsed(coupon.id);
    this.successMessage.set('Coupon marked as used!');
    setTimeout(() => this.successMessage.set(null), 3000);
  }

  /**
   * Check if coupon is expired
   */
  isCouponExpired(coupon: RedeemedCoupon): boolean {
    return this.redemptionService.isCouponExpired(coupon);
  }

  /**
   * Get days remaining for coupon expiry
   */
  getDaysRemaining(coupon: RedeemedCoupon): number {
    const expiryDate = new Date(coupon.expiresAt);
    const now = new Date();
    const diff = Math.ceil((expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));
    return Math.max(0, diff);
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  /**
   * Get coupon partner icon/emoji
   */
  getPartnerIcon(partner: string): string {
    const icons: { [key: string]: string } = {
      'Myntra': '👗',
      'Dominos': '🍕',
      'La Pino\'s Pizza': '🍕',
      'McDonald\'s': '🍔'
    };
    return icons[partner] || '🎁';
  }

  /**
   * Get coupon card color based on discount
   */
  getCouponCardClass(discount: number): string {
    if (discount >= 10) return 'coupon-card premium';
    return 'coupon-card standard';
  }

  goBack(): void {
    this.location.back();
  }
}
