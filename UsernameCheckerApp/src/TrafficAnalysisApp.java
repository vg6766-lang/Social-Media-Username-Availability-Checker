import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficAnalysisApp {
    // Thread-safe maps for high-throughput streaming
    private Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    private Map<String, Set<String>> uniqueUsers = new ConcurrentHashMap<>();
    private Map<String, Integer> sourceCounts = new ConcurrentHashMap<>();

    /**
     * Processes a single page view event in O(1) time.
     */
    public void processEvent(String url, String userId, String source) {
        // 1. Increment total page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // 2. Track unique visitors (Set automatically handles duplicates)
        uniqueUsers.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        // 3. Increment source traffic
        sourceCounts.put(source, sourceCounts.getOrDefault(source, 0) + 1);
    }

    /**
     * Generates dashboard data. Finding Top 10 is O(N log 10).
     */
    public void getDashboard() {
        System.out.println("\n--- REAL-TIME DASHBOARD (Last 5 Seconds) ---");

        // Use a PriorityQueue to find the Top 10 pages efficiently
        PriorityQueue<Map.Entry<String, Integer>> topPages = new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry::getValue)
        );

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            topPages.offer(entry);
            if (topPages.size() > 10) topPages.poll();
        }

        System.out.println("Top Pages:");
        while (!topPages.isEmpty()) {
            Map.Entry<String, Integer> entry = topPages.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int uniques = uniqueUsers.get(url).size();
            System.out.printf("- %s: %d views (%d unique)\n", url, views, uniques);
        }

        System.out.println("\nTraffic Sources:");
        sourceCounts.forEach((src, count) -> System.out.println("- " + src + ": " + count));
    }

    public static void main(String[] args) {
        TrafficAnalysisApp tas = new TrafficAnalysisApp();

        // Simulating incoming stream
        tas.processEvent("/article/breaking-news", "user_1", "google");
        tas.processEvent("/article/breaking-news", "user_2", "facebook");
        tas.processEvent("/article/breaking-news", "user_1", "google"); // Same user
        tas.processEvent("/sports/final", "user_3", "direct");

        tas.getDashboard();
    }
}