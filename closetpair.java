public class closetpair {
    public static int[] closestPair(int[] x_coords, int[] y_coords) {
        // Handle edge cases
        if (x_coords == null || y_coords == null || x_coords.length < 2 || 
            x_coords.length != y_coords.length) {
            return new int[]{-1, -1};
        }
        
        int n = x_coords.length;
        int minDistance = Integer.MAX_VALUE;
        int[] result = new int[2];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Skip if same point
                if (i == j) continue;
                
                int distance = Math.abs(x_coords[i] - x_coords[j]) + 
                             Math.abs(y_coords[i] - y_coords[j]);
                
                if (distance < minDistance || 
                    (distance == minDistance && 
                     (i < result[0] || (i == result[0] && j < result[1])))) {
                    minDistance = distance;
                    result[0] = i;
                    result[1] = j;
                }
            }
        }
        
        return result;
    }

    public static void main(String[] args) {
        // Test case
        int[] x_coords = {1, 2, 3, 2, 4};
        int[] y_coords = {2, 3, 1, 2, 3};
        
        int[] result = closestPair(x_coords, y_coords);
        System.out.println("Closest pair indices: [" + result[0] + ", " + result[1] + "]");
        
        int distance = Math.abs(x_coords[result[0]] - x_coords[result[1]]) + 
                      Math.abs(y_coords[result[0]] - y_coords[result[1]]);
        System.out.println("Minimum distance: " + distance);
    }

}
