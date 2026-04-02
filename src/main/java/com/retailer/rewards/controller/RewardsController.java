package com.retailer.rewards.controller;

import com.retailer.rewards.model.CustomerRewardSummary;
import com.retailer.rewards.model.RewardsResponse;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.service.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/rewards")
@CrossOrigin(origins = "*")
public class RewardsController {

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @GetMapping
    public ResponseEntity<RewardsResponse> getAllRewards() {
        List<CustomerRewardSummary> summaries = rewardsService.calculateAllRewards();
        RewardsResponse response = RewardsResponse.builder()
            .status("SUCCESS")
            .message("Reward points calculated for all customers over the 3-month period.")
            .generatedAt(LocalDateTime.now())
            .customerCount(summaries.size())
            .customers(summaries)
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getRewardsForCustomer(@PathVariable String customerId) {
        Optional<CustomerRewardSummary> summary = rewardsService.calculateRewardsForCustomer(customerId);

        if (summary.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                "status", "NOT_FOUND",
                "message", "No transactions found for customer: " + customerId
            ));
        }

        RewardsResponse response = RewardsResponse.builder()
            .status("SUCCESS")
            .message("Reward points calculated for customer: " + customerId)
            .generatedAt(LocalDateTime.now())
            .customerCount(1)
            .customers(List.of(summary.get()))
            .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(rewardsService.getAllTransactions());
    }

    @GetMapping("/calculate")
    public ResponseEntity<?> calculatePoints(@RequestParam double amount) {
        if (amount < 0) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "BAD_REQUEST",
                "message", "Amount must be a non-negative value."
            ));
        }

        long points = rewardsService.calculatePoints(amount);

        return ResponseEntity.ok(Map.of(
            "amount", amount,
            "points", points,
            "formula", buildFormulaExplanation(amount, points)
        ));
    }

    private String buildFormulaExplanation(double amount, long points) {
        if (amount <= 50) {
            return String.format("$%.2f is at or below $50 -> 0 points", amount);
        } else if (amount <= 100) {
            long band = (long)(amount - 50);
            return String.format("$%.2f -> 1 x $%d (between $50-$100) = %d points", amount, band, points);
        } else {
            long over100 = (long)(amount - 100);
            return String.format("$%.2f -> 2 x $%d (over $100) + 1 x $50 ($50-$100 band) = %d + 50 = %d points",
                amount, over100, 2 * over100, points);
        }
    }
}
