package com.retailer.rewards;

import com.retailer.rewards.data.TransactionDataStore;
import com.retailer.rewards.model.CustomerRewardSummary;
import com.retailer.rewards.service.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RewardsServiceTest {

    private RewardsService rewardsService;

    @BeforeEach
    void setUp() {
        rewardsService = new RewardsService(new TransactionDataStore());
    }

    @Test
    void testBelowFiftyDollars() {
        assertEquals(0, rewardsService.calculatePoints(30.00));
        assertEquals(0, rewardsService.calculatePoints(49.99));
    }

    @Test
    void testExactlyFiftyDollars() {
        assertEquals(0, rewardsService.calculatePoints(50.00));
    }

    @Test
    void testJustOverFiftyDollars() {
        assertEquals(1, rewardsService.calculatePoints(51.00));
    }

    @Test
    void testMidBandAmount() {
        assertEquals(25, rewardsService.calculatePoints(75.00));
    }

    @Test
    void testExactlyOneHundredDollars() {
        assertEquals(50, rewardsService.calculatePoints(100.00));
    }

    @Test
    void testCanonicalExample() {
        assertEquals(90, rewardsService.calculatePoints(120.00));
    }

    @Test
    void testTwoHundredDollars() {
        assertEquals(250, rewardsService.calculatePoints(200.00));
    }

    @Test
    void testFiveHundredDollars() {
        assertEquals(850, rewardsService.calculatePoints(500.00));
    }

    @Test
    void testFractionalAmount() {
        assertEquals(90, rewardsService.calculatePoints(120.99));
        assertEquals(0, rewardsService.calculatePoints(50.99));
        assertEquals(1, rewardsService.calculatePoints(51.99));
    }

    @Test
    void testAllCustomersReturned() {
        assertEquals(5, rewardsService.calculateAllRewards().size());
    }

    @Test
    void testEachCustomerHasThreeMonths() {
        rewardsService.calculateAllRewards().forEach(summary ->
            assertEquals(3, summary.getMonthlyRewards().size())
        );
    }

    @Test
    void testTotalsMatchMonthlySum() {
        rewardsService.calculateAllRewards().forEach(summary -> {
            long monthlySum = summary.getMonthlyRewards().stream().mapToLong(m -> m.getPoints()).sum();
            assertEquals(monthlySum, summary.getTotalPoints());
        });
    }

    @Test
    void testSingleCustomerLookup() {
        Optional<CustomerRewardSummary> result = rewardsService.calculateRewardsForCustomer("C001");
        assertTrue(result.isPresent());
        assertEquals("Alice Johnson", result.get().getCustomerName());
    }

    @Test
    void testUnknownCustomer() {
        assertTrue(rewardsService.calculateRewardsForCustomer("C999").isEmpty());
    }

    @Test
    void testDavidKimIsTopEarner() {
        List<CustomerRewardSummary> summaries = rewardsService.calculateAllRewards();
        long davidPoints = summaries.stream()
            .filter(s -> s.getCustomerId().equals("C004"))
            .findFirst().get().getTotalPoints();

        summaries.stream()
            .filter(s -> !s.getCustomerId().equals("C004"))
            .forEach(s -> assertTrue(davidPoints > s.getTotalPoints()));
    }
}
