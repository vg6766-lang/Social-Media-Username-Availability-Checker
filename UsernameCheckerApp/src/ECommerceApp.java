import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ECommerceApp {
    // Stores: ProductID -> Stock Count
    private HashMap<String, Integer> inventory = new HashMap<>();

    // Stores: ProductID -> Map of UserIDs in order of arrival
    private HashMap<String, LinkedHashMap<Integer, Integer>> waitingLists = new HashMap<>();

    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingLists.put(productId, new LinkedHashMap<>());
    }

    // O(1) Instant Lookup
    public void checkStock(String productId) {
        if (inventory.containsKey(productId)) {
            System.out.println(productId + " - " + inventory.get(productId) + " units available");
        }
    }

    // Synchronized to handle concurrent requests safely
    public synchronized void purchaseItem(String productId, int userId) {
        int currentStock = inventory.getOrDefault(productId, 0);

        if (currentStock > 0) {
            // Success: Reduce stock
            inventory.put(productId, currentStock - 1);
            System.out.println("User " + userId + " -> Success, " + (currentStock - 1) + " units remaining");
        } else {
            // Fail: Add to FIFO waiting list
            LinkedHashMap<Integer, Integer> list = waitingLists.get(productId);
            if (!list.containsKey(userId)) {
                list.put(userId, list.size() + 1);
                System.out.println("User " + userId + " -> Added to waiting list, position #" + list.size());
            }
        }
    }

    public static void main(String[] args) {
        ECommerceApp app = new ECommerceApp();
        String item = "IPHONE15_256GB";

        // Setup
        app.addProduct(item, 100);
        app.checkStock(item);

        // Simulation
        app.purchaseItem(item, 12345); // Success 99
        app.purchaseItem(item, 67890); // Success 98

        // Logic for "After 100 purchases"
        System.out.println("... Fast forward 98 more purchases ...");
        for(int i = 0; i < 98; i++) {
            // Internal logic to deplete stock to 0
        }

        // Directly forcing stock to 0 for demonstration
        app.inventory.put(item, 0);

        // This user hits the waiting list
        app.purchaseItem(item, 99999);
    }
}