package com.retailer.rewards.service;

import com.retailer.rewards.data.TransactionDataStore;
import com.retailer.rewards.model.CustomerRewardSummary;
import com.retailer.rewards.model.MonthlyReward;
import com.retailer.rewards.model.Transaction;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardsService {

    private final TransactionDataStore dataStore;

    public RewardsService(TransactionDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<CustomerRewardSummary> calculateAllRewards() {
        return buildSummaries(dataStore.getAllTransactions());
    }

    public Optional<CustomerRewardSummary> calculateRewardsForCustomer(String customerId) {
        List<Transaction> transactions = dataStore.getAllTransactions().stream()
            .filter(t -> t.getCustomerId().equalsIgnoreCase(customerId))
            .collect(Collectors.toList());

        if (transactions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(buildSummaries(transactions).get(0));
    }

    public List<Transaction> getAllTransactions() {
        return dataStore.getAllTransactions();
    }

    private List<CustomerRewardSummary> buildSummaries(List<Transaction> transactions) {
        Map<String, List<Transaction>> byCustomer = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getCustomerId));

        return byCustomer.entrySet().stream()
            .map(entry -> buildCustomerSummary(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(CustomerRewardSummary::getCustomerId))
            .collect(Collectors.toList());
    }

    private CustomerRewardSummary buildCustomerSummary(String customerId, List<Transaction> txns) {
        String customerName = txns.get(0).getCustomerName();

        Map<String, List<Transaction>> byMonth = txns.stream()
            .collect(Collectors.groupingBy(t ->
                t.getTransactionDate().getYear() + "-" +
                String.format("%02d", t.getTransactionDate().getMonthValue())
            ));

        List<MonthlyReward> monthlyRewards = byMonth.entrySet().stream()
            .map(e -> buildMonthlyReward(e.getKey(), e.getValue()))
            .sorted(Comparator.comparing(MonthlyReward::getYear).thenComparing(MonthlyReward::getMonth))
            .collect(Collectors.toList());

        long totalPoints = monthlyRewards.stream().mapToLong(MonthlyReward::getPoints).sum();
        double totalSpent = txns.stream().mapToDouble(Transaction::getAmount).sum();

        return CustomerRewardSummary.builder()
            .customerId(customerId)
            .customerName(customerName)
            .monthlyRewards(monthlyRewards)
            .totalPoints(totalPoints)
            .totalSpent(Math.round(totalSpent * 100.0) / 100.0)
            .totalTransactions(txns.size())
            .build();
    }

    private MonthlyReward buildMonthlyReward(String yearMonth, List<Transaction> txns) {
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        long points = txns.stream().mapToLong(t -> calculatePoints(t.getAmount())).sum();
        double spent = txns.stream().mapToDouble(Transaction::getAmount).sum();
        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return MonthlyReward.builder()
            .year(year)
            .month(month)
            .monthName(monthName)
            .points(points)
            .totalSpent(Math.round(spent * 100.0) / 100.0)
            .transactionCount(txns.size())
            .build();
    }

    public long calculatePoints(double amount) {
        if (amount <= 50) return 0L;

        if (amount > 100) {
            long dollarsOver100 = (long) (amount - 100);
            return 2 * dollarsOver100 + 50;
        } else {
            return (long) (amount - 50);
        }
    }
}
