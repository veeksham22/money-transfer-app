import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RewardHistoryItem, RewardSummary } from '../models/rewards.model';

@Injectable({ providedIn: 'root' })
export class RewardsService {
  constructor(private http: HttpClient) {}

  getSummary(): Observable<RewardSummary> {
    return this.http.get<RewardSummary>(`${environment.apiBaseUrl}/api/v1/rewards/summary`);
  }

  getHistory(): Observable<RewardHistoryItem[]> {
    return this.http.get<RewardHistoryItem[]>(`${environment.apiBaseUrl}/api/v1/rewards/history`);
  }
}
