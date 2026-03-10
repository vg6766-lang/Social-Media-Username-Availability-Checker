import java.util.*;

class Video {
    String id;
    String title;
    int accessCount = 0;

    Video(String id, String title) {
        this.id = id;
        this.title = title;
    }
}

public class MultiLevelCacheApp {
    private final int L1_CAPACITY = 3; // Kept small for demo
    private final int L2_CAPACITY = 5;
    private final int PROMOTION_THRESHOLD = 2;

    // L1: Fast RAM Cache (LRU)
    private final Map<String, Video> l1Cache = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
        protected boolean removeEldestEntry(Map.Entry<String, Video> eldest) {
            return size() > L1_CAPACITY;
        }
    };

    // L2: SSD Reference Cache
    private final Map<String, Video> l2Cache = new HashMap<>();

    // Statistics
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;

    public Video getVideo(String videoId) {
        totalRequests++;

        // Check L1
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            System.out.println("L1 HIT: " + videoId);
            return l1Cache.get(videoId);
        }

        // Check L2
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            Video v = l2Cache.get(videoId);
            v.accessCount++;
            System.out.println("L2 HIT: " + videoId + " (Accesses: " + v.accessCount + ")");

            if (v.accessCount >= PROMOTION_THRESHOLD) {
                promoteToL1(v);
            }
            return v;
        }

        // Check L3 (Simulated DB)
        l3Hits++;
        System.out.println("L3 MISS - Fetching from DB: " + videoId);
        Video v = new Video(videoId, "Movie " + videoId);
        addToL2(v);
        return v;
    }

    private void promoteToL1(Video v) {
        System.out.println("PROMOTING to L1: " + v.id);
        l2Cache.remove(v.id);
        l1Cache.put(v.id, v);
    }

    private void addToL2(Video v) {
        if (l2Cache.size() >= L2_CAPACITY) {
            // Simple eviction for L2 demo
            String firstKey = l2Cache.keySet().iterator().next();
            l2Cache.remove(firstKey);
        }
        l2Cache.put(v.id, v);
    }

    public void printStats() {
        System.out.println("\n--- Cache Statistics ---");
        System.out.printf("L1 Hit Rate: %.2f%%\n", (l1Hits * 100.0 / totalRequests));
        System.out.printf("L2 Hit Rate: %.2f%%\n", (l2Hits * 100.0 / totalRequests));
        System.out.printf("L3/DB Rate: %.2f%%\n", (l3Hits * 100.0 / totalRequests));
    }

    public static void main(String[] args) {
        MultiLevelCacheApp system = new MultiLevelCacheApp();

        system.getVideo("V1"); // L3 Hit
        system.getVideo("V1"); // L2 Hit
        system.getVideo("V1"); // L2 Hit -> Promoted to L1
        system.getVideo("V1"); // L1 Hit
        system.getVideo("V2"); // L3 Hit

        system.printStats();
    }
}