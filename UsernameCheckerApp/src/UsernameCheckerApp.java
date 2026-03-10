import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UsernameCheckerApp {
    // Primary Registry: Username -> UserId
    private static HashMap<String, Long> userRegistry = new HashMap<>();
    // Popularity Tracker: Username -> Attempt Count
    private static HashMap<String, Integer> attemptTracker = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Mock Data: Pre-registering some users for 10 million scale simulation
        userRegistry.put("john_doe", 1001L);
        userRegistry.put("jane_smith", 1002L);
        userRegistry.put("admin", 1003L);

        System.out.println("--- Social Media Registration System ---");

        while (true) {
            System.out.print("\nEnter username to check (or type 'exit' to quit): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) break;
            if (input.isEmpty()) continue;

            // 1. Check Availability (O(1) Complexity)
            boolean isAvailable = checkAvailability(input);

            if (isAvailable) {
                System.out.println("Result: [TRUE] '" + input + "' is available!");
            } else {
                System.out.println("Result: [FALSE] '" + input + "' is already taken.");

                // 2. Suggest Alternatives
                List<String> suggestions = suggestAlternatives(input);
                System.out.println("Suggestions: " + suggestions);
            }

            // 3. Show Most Attempted (Popularity tracking)
            System.out.println("Trending Check: '" + getMostAttempted() + "' is the most searched name.");
        }

        System.out.println("System shutting down...");
        scanner.close();
    }

    /**
     * O(1) Lookup: Uses the Hash function to find the key instantly.
     */
    public static boolean checkAvailability(String username) {
        String key = username.toLowerCase();

        // Update frequency count
        attemptTracker.put(key, attemptTracker.getOrDefault(key, 0) + 1);

        return !userRegistry.containsKey(key);
    }

    /**
     * Logic: Appends incremental numbers until 3 unique available names are found.
     */
    public static List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int count = 1;
        while (suggestions.size() < 3) {
            String candidate = username.toLowerCase() + count;
            if (!userRegistry.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            count++;
        }
        return suggestions;
    }

    /**
     * Logic: Iterates through the attempt map to find the max value.
     */
    public static String getMostAttempted() {
        String most = "None";
        int max = 0;
        for (Map.Entry<String, Integer> entry : attemptTracker.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                most = entry.getKey();
            }
        }
        return most;
    }
}