public class rewardemployee {
    public static int minRewards(int[] ratings) {
        if (ratings == null || ratings.length == 0) return 0;
        if (ratings.length == 1) return 1;
        
        int n = ratings.length;
        int[] rewards = new int[n];
        java.util.Arrays.fill(rewards, 1);

        // Left to right pass
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) {
                rewards[i] = rewards[i - 1] + 1;
            }
        }

        // Right to left pass
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) {
                rewards[i] = Math.max(rewards[i], rewards[i + 1] + 1);
            }
        }

        // Calculate total rewards
        int total = 0;
        for (int reward : rewards) {
            total += reward;
        }

        return total;
    }

    public static void main(String[] args) {
        int[] ratings1 = {1, 0, 2};
        System.out.println("Test case 1: " + minRewards(ratings1));  // Output: 5

        int[] ratings2 = {1, 2, 2};
        System.out.println("Test case 2: " + minRewards(ratings2));  // Output: 4
    }
}
