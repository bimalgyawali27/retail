package com.retailer.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardSummary {
    private String customerId;
    private String customerName;
    private List<MonthlyReward> monthlyRewards;
    private long totalPoints;
    private double totalSpent;
    private int totalTransactions;
}
