import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { AccountService } from '../../core/services/account.service';
import { RewardsService } from '../../core/services/rewards.service';
import { Account } from '../../core/models/account.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly accountService = inject(AccountService);
  private readonly rewardsService = inject(RewardsService);
  private readonly router = inject(Router);

  account = signal<Account | null>(null);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  userPoints$ = this.rewardsService.getSummary().pipe(
    map(summary => summary.totalPoints)
  );

  ngOnInit(): void {
    const accountId = this.auth.getAccountId();
    if (!accountId) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading.set(true);
    this.accountService.getAccount(accountId).subscribe({
      next: (acc) => {
        console.log(acc);
        this.account.set(acc);
        this.loading.set(false);
      },
      error: (err) => {
        console.log(err);
        this.error.set(err?.error?.message || 'Unable to load account details.');
        this.loading.set(false);
      },
    });
  }

  get holderName(): string {
    return this.auth.getHolderName() || (this.account()?.holderName ?? '');
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  goToTransfer(): void {
    this.router.navigate(['/transfer']);
  }

  goToHistory(): void {
    this.router.navigate(['/history']);
  }

  goToRewards(): void {
    this.router.navigate(['/rewards']);
  }

  goToRedemption(): void {
    this.router.navigate(['/redeem']);
  }
}

