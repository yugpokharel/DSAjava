import java.util.*;

public class PackageDelivery {
    
    // Construct the road network as an adjacency list
    private static Map<Integer, List<Integer>> createGraph(int nodes, int[][] roads) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < nodes; i++) {
            graph.put(i, new ArrayList<>());
        }
        for (int[] road : roads) {
            graph.get(road[0]).add(road[1]);
            graph.get(road[1]).add(road[0]); // Undirected graph
        }
        return graph;
    }

    // Find all locations reachable within 2 roads
    private static Set<Integer> getNearbyNodes(int start, Map<Integer, List<Integer>> graph) {
        Set<Integer> reachable = new HashSet<>();
        Deque<Integer> queue = new ArrayDeque<>();
        int[] distance = new int[graph.size()];
        Arrays.fill(distance, -1);

        queue.offer(start);
        distance[start] = 0;
        reachable.add(start);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : graph.get(current)) {
                if (distance[neighbor] == -1) { // If not visited
                    distance[neighbor] = distance[current] + 1;
                    if (distance[neighbor] <= 2) {
                        reachable.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
        }
        return reachable;
    }

    // Find shortest route between two nodes using BFS
    private static int findShortestPath(int start, int destination, Map<Integer, List<Integer>> graph) {
        if (start == destination) return 0;
        Deque<Integer> queue = new ArrayDeque<>();
        int[] distance = new int[graph.size()];
        Arrays.fill(distance, -1);

        queue.offer(start);
        distance[start] = 0;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : graph.get(current)) {
                if (distance[neighbor] == -1) {
                    distance[neighbor] = distance[current] + 1;
                    if (neighbor == destination) return distance[neighbor];
                    queue.offer(neighbor);
                }
            }
        }
        return Integer.MAX_VALUE; // No valid path
    }

    public static int calculateMinRoads(int[] packages, int[][] roads) {
        int n = packages.length;
        Map<Integer, List<Integer>> graph = createGraph(n, roads);

        List<Integer> packageLocations = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (packages[i] == 1) packageLocations.add(i);
        }

        if (packageLocations.isEmpty()) return 0;

        int minRoads = Integer.MAX_VALUE;

        for (int start = 0; start < n; start++) {
            Set<Integer> collected = new HashSet<>();
            Deque<Integer> queue = new ArrayDeque<>();
            Set<Integer> visited = new HashSet<>();
            queue.offer(start);
            visited.add(start);
            int roadsUsed = 0;
            boolean done = false;

            while (!queue.isEmpty() && !done) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    int current = queue.poll();

                    // Get reachable nodes in 2 steps and check for packages
                    Set<Integer> reachable = getNearbyNodes(current, graph);
                    for (int location : reachable) {
                        if (packages[location] == 1) collected.add(location);
                    }

                    if (collected.size() == packageLocations.size()) {
                        int returnTrip = findShortestPath(current, start, graph);
                        if (returnTrip != Integer.MAX_VALUE) {
                            roadsUsed += returnTrip;
                            minRoads = Math.min(minRoads, roadsUsed);
                        }
                        done = true;
                        break;
                    }

                    // Visit adjacent nodes
                    for (int neighbor : graph.get(current)) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.offer(neighbor);
                        }
                    }
                }
                if (!done) roadsUsed++;
            }
        }

        return minRoads == Integer.MAX_VALUE ? -1 : minRoads;
    }

    public static void main(String[] args) {
        int[] packages1 = {1, 0, 0, 0, 0, 1};
        int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
        System.out.println("Test case 1: " + calculateMinRoads(packages1, roads1));

        int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
        System.out.println("Test case 2: " + calculateMinRoads(packages2, roads2));
    }
}
