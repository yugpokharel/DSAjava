import java.util.*;

public class HashtagAnalyzer {

    public static class HashtagEntry implements Comparable<HashtagEntry> {
        String tag;
        int frequency;

        public HashtagEntry(String tag, int frequency) {
            this.tag = tag;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(HashtagEntry other) {
            if (this.frequency != other.frequency) {
                return Integer.compare(other.frequency, this.frequency);
            }
            return this.tag.compareTo(other.tag);
        }
    }

    public static List<HashtagEntry> analyzeHashtags(List<String> posts) {
        Map<String, Integer> hashtagMap = new HashMap<>();

        for (String post : posts) {
            String[] tokens = post.split("\\s+");

            for (String token : tokens) {
                if (token.startsWith("#")) {
                    String hashtag = token.replaceAll("[^a-zA-Z0-9#]", "");
                    if (hashtag.length() > 1) {
                        hashtagMap.put(hashtag, hashtagMap.getOrDefault(hashtag, 0) + 1);
                    }
                }
            }
        }

        List<HashtagEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : hashtagMap.entrySet()) {
            entries.add(new HashtagEntry(entry.getKey(), entry.getValue()));
        }

        Collections.sort(entries);

        return entries;
    }

    public static void displayResults(List<HashtagEntry> hashtagList) {
        System.out.println("Hashtag Frequency Table:");
        System.out.println("+------------+--------+");
        System.out.println("| Hashtag    | Count  |");
        System.out.println("+------------+--------+");

        for (HashtagEntry entry : hashtagList) {
            System.out.printf("| %-10s | %6d |\n", entry.tag, entry.frequency);
        }

        System.out.println("+------------+--------+");
    }

    public static void main(String[] args) {
        // Sample tweets for testing
        List<String> sampleTweets = Arrays.asList(
            "Tweet 13 #HappyDay",
            "Tweet 14 #HappyDay",
            "Tweet 17 #HappyDay",
            "Tweet 15 #TechLife",
            "Tweet 18 #TechLife"
        );

        List<HashtagEntry> hashtagResults = analyzeHashtags(sampleTweets);
        displayResults(hashtagResults);
    }
}

