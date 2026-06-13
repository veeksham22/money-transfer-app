import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe, Location } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { RewardsService } from '../../core/services/rewards.service';
import { RewardHistoryItem, RewardSummary } from '../../core/models/rewards.model';

@Component({
  selector: 'app-rewards',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './rewards.component.html',
  styleUrl: './rewards.component.scss',
})
export class RewardsComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly rewardsService = inject(RewardsService);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  summary = signal<RewardSummary | null>(null);
  history = signal<RewardHistoryItem[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  ngOnInit(): void {
    if (!this.auth.getAccountId()) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading.set(true);
    this.rewardsService.getSummary().subscribe({
      next: (summary) => {
        this.summary.set(summary);
        this.loadHistory();
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Unable to load rewards summary.');
        this.loading.set(false);
      },
    });
  }

  private loadHistory(): void {
    this.rewardsService.getHistory().subscribe({
      next: (items) => {
        this.history.set(items);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Unable to load reward history.');
        this.loading.set(false);
      },
    });
  }

  goBack(): void {
    this.location.back();
  }
}
