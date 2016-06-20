

import java.io.*;
import java.util.*;

/*
 This is the main class for the project
 In this file, we build a random unperfect maze with N * N cells
 Then, we transfer the maze to graph structure with N * N vertices
 We evaluate BFS, Bellman Ford, A* for single-source shortest path problem
 Also, evalute BFS, Bellman Ford, Floyd Warshall for all-pairs shortest path problem
 Besides, we visulize the shortest path to ensure the correctness

 In this project, we use and modify the code from "reference: http://algs4.cs.princeton.edu/"
 */
public class Roomba {

    private int N;
    private boolean[][] north;
    private boolean[][] east;
    private boolean[][] south;
    private boolean[][] west;
    private boolean[][] visited;
    private boolean done = false;
    private int dockingStationPosition;
    private int roombaPosition;
    public Graph G;
    public BreadthFirstPaths bfs;
    public EdgeWeightedDigraph EWD;
    public BellmanFordSP BF;
    AdjMatrixEdgeWeightedDigraph AdjMatrixG;
    public FloydWarshall FW;
    public AStarSearch aStar;

    public Roomba(int N) {
        this.N = N;
        StdDraw.setXscale(0, N + 2);
        StdDraw.setYscale(0, N + 2);
        init();
        generate();
    }

    private void init() {
        visited = new boolean[N + 2][N + 2];
        for (int x = 0; x < N + 2; x++) {
            visited[x][0] = visited[x][N + 1] = true;
        }
        for (int y = 0; y < N + 2; y++) {
            visited[0][y] = visited[N + 1][y] = true;
        }

        north = new boolean[N + 2][N + 2];
        east = new boolean[N + 2][N + 2];
        south = new boolean[N + 2][N + 2];
        west = new boolean[N + 2][N + 2];
        for (int x = 0; x < N + 2; x++) {
            for (int y = 0; y < N + 2; y++) {
                north[x][y] = east[x][y] = south[x][y] = west[x][y] = true;
            }
        }
    }

    private void generate(int x, int y) {
        visited[x][y] = true;

        while (!visited[x][y + 1] || !visited[x + 1][y] || !visited[x][y - 1] || !visited[x - 1][y]) {

            while (true) {
                double r = Math.random();
                if (r < 0.25 && !visited[x][y + 1]) {
                    north[x][y] = south[x][y + 1] = false;
                    generate(x, y + 1);
                    break;
                } else if (r >= 0.25 && r < 0.50 && !visited[x + 1][y]) {
                    east[x][y] = west[x + 1][y] = false;
                    generate(x + 1, y);
                    break;
                } else if (r >= 0.5 && r < 0.75 && !visited[x][y - 1]) {
                    south[x][y] = north[x][y - 1] = false;
                    generate(x, y - 1);
                    break;
                } else if (r >= 0.75 && r < 1.00 && !visited[x - 1][y]) {
                    west[x][y] = east[x - 1][y] = false;
                    generate(x - 1, y);
                    break;
                }
            }
        }
    }

    private void generate() {
        generate(1, 1);

        for (int i = 0; i < N * N; i++) {
            int x = (int) (1 + Math.random() * (N - 1));
            int y = (int) (1 + Math.random() * (N - 1));
            north[x][y] = south[x][y + 1] = false;
        }

    }

    public void drawRoomba() {
        StdDraw.setPenColor(StdDraw.RED);

        // roomba position
        StdDraw.filledCircle(roombaPosition % N + 1.5, roombaPosition / N + 1.5, 0.375);

    }

    public void drawStation() {
        StdDraw.setPenColor(StdDraw.GREEN);

        // docking station position
        StdDraw.filledCircle(dockingStationPosition % N + 1.5, dockingStationPosition / N + 1.5, 0.375);
    }

    // draw the maze
    public void drawMaze() {

        StdDraw.setPenColor(StdDraw.BLACK);
        for (int x = 1; x <= N; x++) {
            for (int y = 1; y <= N; y++) {
                if (south[x][y]) {
                    StdDraw.line(x, y, x + 1, y);
                }
                if (north[x][y]) {
                    StdDraw.line(x, y + 1, x + 1, y + 1);
                }
                if (west[x][y]) {
                    StdDraw.line(x, y, x, y + 1);
                }
                if (east[x][y]) {
                    StdDraw.line(x + 1, y, x + 1, y + 1);
                }
            }
        }

        StdDraw.show(1000);
    }

    public void transferGraph() {
        G = new Graph(N * N);
        EWD = new EdgeWeightedDigraph(N * N);
        AdjMatrixG = new AdjMatrixEdgeWeightedDigraph(N * N);

        for (int x = 1; x <= N; x++) {
            for (int y = 1; y <= N; y++) {
                //if (!south[x][y]) G.addEdge((x-1) + (y-1) * N, (x-1) + (y-2) * N);
                if (!north[x][y]) {
                    int v = (x - 1) + (y - 1) * N;
                    int w = (x - 1) + y * N;
                    G.addEdge(v, w);
                    DirectedEdge e1 = new DirectedEdge(v, w, 1);
                    DirectedEdge e2 = new DirectedEdge(w, v, 1);
                    EWD.addEdge(e1);
                    EWD.addEdge(e2);
                    AdjMatrixG.addEdge(e1);
                    AdjMatrixG.addEdge(e2);
                }
                //if (!west[x][y])  G.addEdge((x-1) + (y-1) * N, (x-2) + (y-1) * N);
                if (!east[x][y]) {
                    int v = (x - 1) + (y - 1) * N;
                    int w = x + (y - 1) * N;
                    G.addEdge(v, w);
                    DirectedEdge e1 = new DirectedEdge(v, w, 1);
                    DirectedEdge e2 = new DirectedEdge(w, v, 1);
                    EWD.addEdge(e1);
                    EWD.addEdge(e2);
                    AdjMatrixG.addEdge(e1);
                    AdjMatrixG.addEdge(e2);
                }
            }
        }

    }

    public void computePathsFlodyWarshall() {

        FW = new FloydWarshall(AdjMatrixG);

    }

    public void computePathsBellmanFord(int s) {

        BF = new BellmanFordSP(EWD, s);

    }

    public void computePathsBFS(int s) {

        bfs = new BreadthFirstPaths(G, s);

    }

    public void computeAStarPath(int roombaPosition, int dockingStationPosition) {
        AStarSearch aStar = new AStarSearch();
        List<Node> path = aStar.searchPath(G, roombaPosition, dockingStationPosition);

        //for (Node n : path) {
        //    int x = n.value;
        //    StdDraw.setPenColor(StdDraw.BLUE);
        //    StdDraw.filledCircle(x % N + 1.5, x / N + 1.5, 0.25);
        //    StdDraw.show(30);
        //}
    }

    public void drawPathFloydWarshall() {

        System.out.println();
        System.out.println("shortest path from rooba to docking station: ");

        if (FW.hasPath(roombaPosition, dockingStationPosition)) {
            System.out.print("Shortest path from vertex " + roombaPosition + " to vertex " + dockingStationPosition + ": ");

            for (DirectedEdge e : FW.path(roombaPosition, dockingStationPosition)) {
                int x = e.from();

                if (x == roombaPosition) {
                    StdOut.print(x);
                } else {
                    StdOut.print("-" + x);
                }

                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.filledCircle(x % N + 1.5, x / N + 1.5, 0.25);
                StdDraw.show(30);

            }

            StdOut.println();
        } else {
            StdOut.printf("%d to %d (-):  not connected\n", dockingStationPosition, roombaPosition);
        }

    }

    public void drawPathBellmanFord() {
        System.out.println();
        System.out.println("shortest path from rooba to docking station: ");

        if (BF.hasPathTo(roombaPosition)) {
            System.out.print("Shortest path from vertex " + roombaPosition + " to vertex " + dockingStationPosition + ": ");

            Stack<Integer> reverse = new Stack<Integer>();
            for (DirectedEdge e : BF.pathTo(roombaPosition)) {
                int x = e.from();
                reverse.push(x);
            }

            while (!reverse.isEmpty()) {
                int x = reverse.pop();
                if (x == roombaPosition) {
                    StdOut.print(x);
                } else {
                    StdOut.print("-" + x);
                }

                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.filledCircle(x % N + 1.5, x / N + 1.5, 0.25);
                StdDraw.show(30);

            }

            StdOut.println();
        } else {
            StdOut.printf("%d to %d (-):  not connected\n", dockingStationPosition, roombaPosition);
        }

    }

    public void drawPathBFS() {

        System.out.println();
        System.out.println("shortest path from rooba to docking station: ");
        if (bfs.hasPathTo(roombaPosition)) {
            StdOut.printf("%d to %d (%d):  ", roombaPosition, dockingStationPosition, bfs.distTo(roombaPosition));
            Stack<Integer> reverse = new Stack<Integer>();
            for (int x : bfs.pathTo(roombaPosition)) {
                reverse.push(x);
            }

            while (!reverse.isEmpty()) {
                int x = reverse.pop();
                if (x == roombaPosition) {
                    StdOut.print(x);
                } else {
                    StdOut.print("-" + x);
                }

                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.filledCircle(x % N + 1.5, x / N + 1.5, 0.25);
                StdDraw.show(30);

            }

            StdOut.println();
        } else {
            StdOut.printf("%d to %d (-):  not connected\n", dockingStationPosition, roombaPosition);
        }
    }

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int N = 0;

        System.out.println("Enter the size of floor plan N (e.g. 30):");
        try {
            N = Integer.parseInt(br.readLine());
        } catch (IOException ioe) {

        }

        Roomba floor = new Roomba(N);
        floor.transferGraph();
        System.out.println("The graph structure of the floor plan generated:");
        System.out.println(floor.G);

        System.out.println("Experiment of single-source");
        long start0 = System.nanoTime();
        //for (int s = 0; s < (N * N); s++) {
        floor.computePathsBFS(0);
        //}
        long time0 = System.nanoTime() - start0;
        System.out.println("BFS N: " + N + " V: " + floor.G.V() + " E: " + floor.G.E());
        System.out.println("BFS running time: " + time0 / 1000000.0 + "ms");

        long start1 = System.nanoTime();
        //for (int s = 0; s < (N * N); s++) {
        floor.computePathsBellmanFord(0);
        //}
        long end1 = System.nanoTime();
        System.out.println("Bellman-Ford N: " + N + " V: " + floor.EWD.V() + " E: " + floor.EWD.E());
        System.out.println("Bellman-Ford running time: " + (end1 - start1) / 1000000.0 + "ms");

        long start2 = System.nanoTime();
        //for (int i = 0; i < N * N; i++) {
        for (int j = 0; j < N * N; j++) {

            floor.computeAStarPath(j, floor.dockingStationPosition);
        }
        //}
        long end2 = System.nanoTime();
        //System.out.println("AStarSearch N: " + N + " V: " + maze.AdjMatrixG.V() + " E: " + maze.AdjMatrixG.E());
        System.out.println("AStarSearch running time: " + (end2 - start2) / 1000000.0 + "ms");

        System.out.println();
        System.out.println("Experiment of all-pairs:");

        long start4 = System.nanoTime();
        for (int s = 0; s < (N * N); s++) {
            floor.computePathsBFS(s);
        }
        long time4 = System.nanoTime() - start4;
        System.out.println("BFS N: " + N + " V: " + floor.G.V() + " E: " + floor.G.E());
        System.out.println("BFS running time: " + time4 / 1000000.0 + "ms");

        long start5 = System.nanoTime();
        for (int s = 0; s < (N * N); s++) {
            floor.computePathsBellmanFord(s);
        }
        long end5 = System.nanoTime();
        System.out.println("Bellman-Ford N: " + N + " V: " + floor.EWD.V() + " E: " + floor.EWD.E());
        System.out.println("Bellman-Ford running time: " + (end5 - start5) / 1000000.0 + "ms");

        long start3 = System.nanoTime();
        floor.computePathsFlodyWarshall();
        long end3 = System.nanoTime();
        System.out.println("FloydWarshall N: " + N + " V: " + floor.AdjMatrixG.V() + " E: " + floor.AdjMatrixG.E());
        System.out.println("FloydWarshall running time: " + (end3 - start3) / 1000000.0 + "ms");

        System.out.println("Start the visulization:");

        while (true) {

            System.out.println();
            System.out.println("Enter anything to see new floor plan");
            try {
                br.readLine();
            } catch (IOException ioe) {

            }

            StdDraw.clear();
            StdDraw.show(0);
            floor.drawMaze();

            System.out.println("Enter the position of station (MUST < N^2): ");
            try {
                floor.dockingStationPosition = Integer.parseInt(br.readLine());

                if (floor.dockingStationPosition > N * N - 1) {
                    System.out.println("the position(index of vertex) is out of range");
                    System.exit(1);
                }
            } catch (IOException ioe) {

            }

            StdDraw.show(0);
            floor.drawStation();

            System.out.println("Enter the position of roomba (MUST < N^2): ");
            try {
                floor.roombaPosition = Integer.parseInt(br.readLine());

                if (floor.roombaPosition > N * N - 1) {
                    System.out.println("the position(index of vertex) is out of range");
                    System.exit(1);
                }
            } catch (IOException ioe) {

            }

            StdDraw.show(0);
            floor.drawRoomba();
            StdDraw.show(0);
            floor.drawPathFloydWarshall();

        }

    }

}
