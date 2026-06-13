export interface RewardSummary {
  username: string;
  totalPoints: number;
  totalGrants: number;
  minEligibleAmount: number;
  pointsPerHundredRupees: number;
}

export interface RewardHistoryItem {
  rewardId: number;
  transactionId: number;
  fromAccountId: number;
  toAccountId: number;
  transactionAmount: number;
  points: number;
  grantedAt: string;
}
