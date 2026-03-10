import java.util.*;

public class AutoCompleteApp {

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        // Stores the top 10 queries for the prefix ending at this node
        PriorityQueue<String> topSuggestions = new PriorityQueue<>(
                (a, b) -> globalCounts.get(a).equals(globalCounts.get(b)) ?
                        b.compareTo(a) : globalCounts.get(a) - globalCounts.get(b)
        );
    }

    private TrieNode root = new TrieNode();
    private Map<String, Integer> globalCounts = new HashMap<>();

    /**
     * Updates query frequency and re-ranks the Trie.
     * Time Complexity: O(L log 10) where L is query length.
     */
    public void updateFrequency(String query) {
        globalCounts.put(query, globalCounts.getOrDefault(query, 0) + 1);

        TrieNode curr = root;
        for (char c : query.toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);
            updateTopK(curr, query);
        }
    }

    private void updateTopK(TrieNode node, String query) {
        if (!node.topSuggestions.contains(query)) {
            node.topSuggestions.offer(query);
            if (node.topSuggestions.size() > 10) node.topSuggestions.poll();
        } else {
            // Re-sort the existing queue (In production, use a more efficient update)
            List<String> temp = new ArrayList<>(node.topSuggestions);
            node.topSuggestions.clear();
            node.topSuggestions.addAll(temp);
        }
    }

    /**
     * Returns top suggestions in O(L) time.
     */
    public List<String> search(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            if (!curr.children.containsKey(c)) return Collections.emptyList();
            curr = curr.children.get(c);
        }

        List<String> results = new ArrayList<>(curr.topSuggestions);
        results.sort((a, b) -> globalCounts.get(b) - globalCounts.get(a));
        return results;
    }

    public static void main(String[] args) {
        AutoCompleteApp ac = new AutoCompleteApp();
        ac.updateFrequency("java tutorial");
        ac.updateFrequency("javascript");
        ac.updateFrequency("java download");

        System.out.println("Suggestions for 'jav': " + ac.search("jav"));
    }
}