import java.util.*;

class Transaction {
    int id;
    double amount;
    String merchant;
    String account;

    Transaction(int id, double amount, String merchant, String account) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
    }

    @Override
    public String toString() {
        return "{id:" + id + ", amt:" + amount + ", merchant:'" + merchant + "'}";
    }
}

public class TwoSumApp {

    // 1. Classic Two-Sum: Finds pairs that sum to a target amount
    // Time Complexity: O(n) using Hash Lookup
    public static void findTwoSum(List<Transaction> txns, double target) {
        System.out.println("\n--- Detecting Two-Sum Fraud (Target: " + target + ") ---");
        Map<Double, Transaction> complementMap = new HashMap<>();

        for (Transaction t : txns) {
            double complement = target - t.amount;
            if (complementMap.containsKey(complement)) {
                System.out.println("Match Found: " + complementMap.get(complement) + " + " + t);
            }
            complementMap.put(t.amount, t);
        }
    }

    // 2. Duplicate Detection: Same amount, same merchant, different accounts
    // Time Complexity: O(n) using Composite Keys
    public static void detectDuplicates(List<Transaction> txns) {
        System.out.println("\n--- Detecting Duplicate Transactions ---");
        // Key: "Amount|Merchant" -> Value: List of Transactions
        Map<String, List<Transaction>> history = new HashMap<>();

        for (Transaction t : txns) {
            String key = t.amount + "|" + t.merchant;

            if (history.containsKey(key)) {
                for (Transaction existing : history.get(key)) {
                    if (!existing.account.equals(t.account)) {
                        System.out.println("ALERT: Duplicate found across accounts! "
                                + existing.account + " and " + t.account + " @ " + t.merchant);
                    }
                }
            }
            history.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }
    }

    public static void main(String[] args) {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1, 500.0, "Store A", "ACC_1"),
                new Transaction(2, 300.0, "Store B", "ACC_2"),
                new Transaction(3, 200.0, "Store C", "ACC_3"),
                new Transaction(4, 500.0, "Store A", "ACC_4") // Potential duplicate
        );

        findTwoSum(transactions, 500.0); // Should find 300 + 200
        detectDuplicates(transactions);  // Should find Store A duplicates
    }
}