package com.retailer.rewards.data;

import com.retailer.rewards.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class TransactionDataStore {

    private final List<Transaction> transactions;

    public TransactionDataStore() {
        this.transactions = buildDataSet();
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    private List<Transaction> buildDataSet() {
        Transaction t1  = new Transaction("TXN-001", "C001", "Alice Johnson", LocalDate.of(2024, 1, 5), 120.00);
        Transaction t2  = new Transaction("TXN-002", "C001", "Alice Johnson", LocalDate.of(2024, 1, 14), 85.50);
        Transaction t3  = new Transaction("TXN-003", "C001", "Alice Johnson", LocalDate.of(2024, 1, 22), 210.75);
        Transaction t4  = new Transaction("TXN-004", "C001", "Alice Johnson", LocalDate.of(2024, 2, 3), 150.00);
        Transaction t5  = new Transaction("TXN-005", "C001", "Alice Johnson", LocalDate.of(2024, 2, 18), 45.00);

        Transaction t6  = new Transaction("TXN-006", "C002", "Bob Martinez", LocalDate.of(2024, 1, 8), 75.00);
        Transaction t7  = new Transaction("TXN-007", "C002", "Bob Martinez", LocalDate.of(2024, 2, 6), 130.00);
        Transaction t8  = new Transaction("TXN-008", "C002", "Bob Martinez", LocalDate.of(2024, 2, 20), 60.00);
        Transaction t9  = new Transaction("TXN-009", "C002", "Bob Martinez", LocalDate.of(2024, 3, 12), 175.50);
        Transaction t10 = new Transaction("TXN-010", "C002", "Bob Martinez", LocalDate.of(2024, 3, 28), 55.00);

        Transaction t11 = new Transaction("TXN-011", "C003", "Carol Chen", LocalDate.of(2024, 1, 10), 40.00);
        Transaction t12 = new Transaction("TXN-012", "C003", "Carol Chen", LocalDate.of(2024, 2, 14), 100.00);
        Transaction t13 = new Transaction("TXN-013", "C003", "Carol Chen", LocalDate.of(2024, 3, 3), 115.00);

        Transaction t14 = new Transaction("TXN-014", "C004", "David Kim", LocalDate.of(2024, 1, 3), 500.00);
        Transaction t15 = new Transaction("TXN-015", "C004", "David Kim", LocalDate.of(2024, 3, 5), 420.00);

        return Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15);
    }
}
