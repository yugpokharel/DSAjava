import java.util.*;

public class unionfind {
    // Disjoint Set Union (DSU) structure
    static class DSU {
        private int[] parent, rank;

        public DSU(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);  // Path compression
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            if (rootX == rootY) return;

            if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }

    public static int connectDevices(int n, int[] costs, int[][] links) {
        if (n <= 0 || costs == null || costs.length != n) return 0;

        List<int[]> allEdges = new ArrayList<>();

        // Add edges from virtual node to each module
        for (int i = 0; i < n; i++) {
            allEdges.add(new int[]{n, i, costs[i]});
        }

        // Add given connections as edges
        for (int[] link : links) {
            allEdges.add(new int[]{link[0] - 1, link[1] - 1, link[2]});
        }

        // Sort edges by cost (ascending)
        allEdges.sort(Comparator.comparingInt(a -> a[2]));

        DSU dsu = new DSU(n + 1);
        int totalCost = 0, connected = 0;

        // Kruskal's MST algorithm
        for (int[] edge : allEdges) {
            int node1 = edge[0], node2 = edge[1], cost = edge[2];

            if (dsu.find(node1) != dsu.find(node2)) {
                dsu.union(node1, node2);
                totalCost += cost;
                connected++;
                if (connected == n) break;
            }
        }

        return connected == n ? totalCost : -1;
    }

    public static void main(String[] args) {
        int n = 3;
        int[] moduleCosts = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};

        int result = connectDevices(n, moduleCosts, connections);
        System.out.println("Minimum cost to connect all devices: " + result);
    }
}
