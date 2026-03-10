import java.util.*;

public class PlagiarismDetectionApp {
    // Maps an n-gram to a set of Document IDs that contain it
    private static Map<String, Set<String>> globalIndex = new HashMap<>();
    private static final int N = 5; // Using 5-grams for detection

    public static void main(String[] args) {
        // Mock Database: Pre-indexing existing essays
        indexDocument("essay_089.txt", "The quick brown fox jumps over the lazy dog consistently.");
        indexDocument("essay_092.txt", "Academic integrity is the foundation of higher learning and research.");

        // Testing a new submission
        String newDocContent = "The quick brown fox jumps over a lazy cat frequently.";
        analyzeDocument("essay_123.txt", newDocContent);
    }

    /**
     * Breaks text into n-grams and adds them to the global hash map.
     */
    public static void indexDocument(String docId, String content) {
        List<String> nGrams = generateNGrams(content);
        for (String gram : nGrams) {
            globalIndex.putIfAbsent(gram, new HashSet<>());
            globalIndex.get(gram).add(docId);
        }
    }

    /**
     * Compares a new document against the global index to find matches.
     */
    public static void analyzeDocument(String docId, String content) {
        List<String> nGrams = generateNGrams(content);
        int totalNGrams = nGrams.size();

        // Count how many times each existing document matches an n-gram in the new doc
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : nGrams) {
            if (globalIndex.containsKey(gram)) {
                for (String matchingDocId : globalIndex.get(gram)) {
                    matchCounts.put(matchingDocId, matchCounts.getOrDefault(matchingDocId, 0) + 1);
                }
            }
        }

        System.out.println("Analyzing " + docId + "...");
        System.out.println("-> Extracted " + totalNGrams + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / totalNGrams;
            String status = (similarity > 50) ? "PLAGIARISM DETECTED" : "suspicious";

            System.out.printf("-> Found %d matching n-grams with %s\n", entry.getValue(), entry.getKey());
            System.out.printf("-> Similarity: %.1f%% (%s)\n", similarity, status);
        }
    }

    /**
     * Helper to split text into sliding windows of N words.
     */
    private static List<String> generateNGrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        List<String> nGrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(j < N - 1 ? " " : "");
            }
            nGrams.add(sb.toString());
        }
        return nGrams;
    }
}
