import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { RedeemedCoupon, CouponOffer, RedemptionHistory, COUPON_OFFERS } from '../models/coupon.model';

interface CouponRedemptionRequest {
  pointsToSpend: number;
  merchant: string;
}

interface CouponRedemptionResponse {
  id: number;
  couponCode: string;
  merchant: string;
  discountPercentage: number;
  pointsSpent: number;
  status: string;
  redeemedAt: string;
  expiryDate: string;
  remainingPoints: number;
}

@Injectable({ providedIn: 'root' })
export class RedemptionService {
  private readonly STORAGE_KEY = 'mts_redeemed_coupons';
  private readonly API_URL = `${environment.apiBaseUrl}/api/v1/redemptions`;
  
  private redeemedCouponsSubject = new BehaviorSubject<RedeemedCoupon[]>(this.loadFromStorage());

  constructor(private http: HttpClient) {}

  /**
   * Redeem points for a coupon via backend API
   * Points are deducted from user's account
   * Returns generated coupon code
   */
  redeemCoupon(offerPoints: number, merchant: string): Observable<CouponRedemptionResponse> {
    const request: CouponRedemptionRequest = {
      pointsToSpend: offerPoints,
      merchant: merchant
    };

    return this.http.post<CouponRedemptionResponse>(`${this.API_URL}/redeem`, request).pipe(
      tap((response: CouponRedemptionResponse) => {
        // Convert backend response to RedeemedCoupon and cache locally
        const redeemedCoupon: RedeemedCoupon = {
          id: response.id.toString(),
          couponCode: response.couponCode,
          partner: response.merchant,
          discountPercentage: response.discountPercentage,
          pointsRedeemed: response.pointsSpent,
          redeemedAt: response.redeemedAt,
          expiresAt: response.expiryDate,
          isUsed: false
        };
        this.saveCoupon(redeemedCoupon);
      })
    );
  }

  /**
   * Get all user redeemed coupons from backend
   */
  getUserCoupons(): Observable<RedeemedCoupon[]> {
    return this.http.get<RedeemedCoupon[]>(`${this.API_URL}/my-coupons`).pipe(
      tap((coupons: RedeemedCoupon[]) => {
        this.redeemedCouponsSubject.next(coupons);
        this.saveToStorage(coupons);
      })
    );
  }

  /**
   * Get active coupons from backend
   */
  getActiveCouponsFromBackend(): Observable<RedeemedCoupon[]> {
    return this.http.get<RedeemedCoupon[]>(`${this.API_URL}/active`).pipe(
      tap((coupons: RedeemedCoupon[]) => {
        // Update subject with active coupons
        const allCoupons = this.redeemedCouponsSubject.value;
        const filteredCoupons = allCoupons.filter(c => coupons.some(ac => ac.id === c.id));
        this.redeemedCouponsSubject.next(filteredCoupons);
      })
    );
  }

  /**
   * Get available points from backend (after deducting redeemed)
   */
  getAvailablePointsFromBackend(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/available-points`);
  }

  /**
   * Get available coupon offers that user can redeem
   * Filters based on points they have
   */
  getAvailableCoupons(currentPoints: number): CouponOffer[] {
    return COUPON_OFFERS.filter(offer => offer.pointsRequired <= currentPoints);
  }

  /**
   * Get all redeemed coupons (local cache)
   */
  getRedeemedCoupons(): Observable<RedeemedCoupon[]> {
    return this.redeemedCouponsSubject.asObservable();
  }

  /**
   * Get redemption history (available and used coupons)
   */
  getRedemptionHistory(): RedemptionHistory {
    const allCoupons = this.redeemedCouponsSubject.value;
    const availableCoupons = allCoupons.filter(c => !c.isUsed && !this.isCouponExpired(c));
    const usedCoupons = allCoupons.filter(c => c.isUsed);

    return {
      totalCouponsRedeemed: allCoupons.length,
      availableCoupons,
      usedCoupons
    };
  }

  /**
   * Mark a coupon as used
   */
  markCouponAsUsed(couponId: string): void {
    const coupons = this.redeemedCouponsSubject.value;
    const coupon = coupons.find(c => c.id === couponId);

    if (coupon) {
      coupon.isUsed = true;
      coupon.usedAt = new Date().toISOString();
      this.saveToStorage(coupons);
      this.redeemedCouponsSubject.next([...coupons]);
    }
  }

  /**
   * Get single coupon details
   */
  getCouponById(couponId: string): RedeemedCoupon | undefined {
    return this.redeemedCouponsSubject.value.find(c => c.id === couponId);
  }

  /**
   * Check if coupon is expired
   */
  isCouponExpired(coupon: RedeemedCoupon): boolean {
    return new Date(coupon.expiresAt) < new Date();
  }

  /**
   * Get active (non-expired, unused) coupons from local cache
   */
  getActiveCoupons(): RedeemedCoupon[] {
    return this.redeemedCouponsSubject.value.filter(
      c => !c.isUsed && !this.isCouponExpired(c)
    );
  }

  /**
   * Clear all coupons (for logout or reset)
   */
  clearAllCoupons(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this.redeemedCouponsSubject.next([]);
  }

  // ============ Private Helper Methods ============

  private saveCoupon(coupon: RedeemedCoupon): void {
    const coupons = this.redeemedCouponsSubject.value;
    coupons.push(coupon);
    this.saveToStorage(coupons);
    this.redeemedCouponsSubject.next([...coupons]);
  }

  private saveToStorage(coupons: RedeemedCoupon[]): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(coupons));
  }

  private loadFromStorage(): RedeemedCoupon[] {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      return stored ? JSON.parse(stored) : [];
    } catch (error) {
      console.error('Error loading redeemed coupons from storage:', error);
      return [];
    }
  }
}
