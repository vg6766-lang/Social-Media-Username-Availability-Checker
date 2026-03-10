import java.util.HashMap;
import java.util.Map;

// Custom Entry class to store IP and timing data
class DNSEntry {
    String ipAddress;
    long expiryTime; // Store as a timestamp (milliseconds)

    public DNSEntry(String ipAddress, int ttlSeconds) {
        this.ipAddress = ipAddress;
        // Current time + TTL converted to ms
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCacheApp {
    private HashMap<String, DNSEntry> cache = new HashMap<>();
    private int hits = 0;
    private int misses = 0;

    public String resolve(String domain) {
        long startTime = System.nanoTime();

        // Check if domain exists in our HashMap
        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            // Check if the entry is still valid (TTL check)
            if (!entry.isExpired()) {
                hits++;
                long duration = System.nanoTime() - startTime;
                System.out.println("resolve(\"" + domain + "\") -> Cache HIT -> " + entry.ipAddress + " (retrieved in " + (duration / 1_000_000.0) + "ms)");
                return entry.ipAddress;
            } else {
                System.out.println("resolve(\"" + domain + "\") -> Cache EXPIRED");
                cache.remove(domain); // Remove stale data
            }
        }

        // Cache MISS: Simulate a slow upstream DNS query
        misses++;
        String ip = "172.217.14." + (int)(Math.random() * 255);
        simulateSlowQuery();

        // Store in cache with a 5-second TTL for this demo
        cache.put(domain, new DNSEntry(ip, 5));

        System.out.println("resolve(\"" + domain + "\") -> Cache MISS -> Query upstream -> " + ip + " (TTL: 5s)");
        return ip;
    }

    private void simulateSlowQuery() {
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }

    public void getCacheStats() {
        double total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits / total) * 100;
        System.out.println("\n--- DNS Cache Statistics ---");
        System.out.println("Hit Rate: " + hitRate + "%");
        System.out.println("Total Hits: " + hits);
        System.out.println("Total Misses: " + misses);
    }

    public static void main(String[] args) throws InterruptedException {
        DNSCacheApp dns = new DNSCacheApp();

        // First attempt (Miss)
        dns.resolve("google.com");

        // Second attempt (Hit - should be very fast)
        dns.resolve("google.com");

        System.out.println("\n... Waiting 6 seconds for TTL to expire ...");
        Thread.sleep(6000);

        // Third attempt (Expired -> Miss)
        dns.resolve("google.com");

        dns.getCacheStats();
    }
}