

import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;

public class AStarSearch{

    //public static List<Node> printPath(Node target){
    //    List<Node> path = new ArrayList<Node>();

    //    for(Node node = target; node!=null; node = node.parent){
    //        path.add(node);
    //    }

    //    Collections.reverse(path);

    //    return path;
    //}

    public static List<Node> searchPath(Graph g, int start, int end){

        Set<Node> explored = new HashSet<Node>();

        PriorityQueue<Node> queue = new PriorityQueue<Node>(g.V(), 
                new Comparator<Node>(){
                    //override compare method
                    public int compare(Node i, Node j){
                        if(i.f_scores > j.f_scores){
                            return 1;
                        }

                        else if (i.f_scores < j.f_scores){
                            return -1;
                        }

                        else{
                            return 0;
                        }
                    }
                }
        );

        // Convert Graph into Nodes and Edges
        Node[] nodes = new Node[g.V()];
        int width = (int) Math.sqrt(g.V());
        for (int v = 0; v < g.V(); v++) {
            // Calculate heuristic value for each vertex
            int row1 = v / width;
            int col1 = v % width;
            int row2 = end / width;
            int col2 = end % width;
            int hVal = Math.abs(row1 - row2) + Math.abs(col1 - col2);

            // Create node for each vertex
            nodes[v] = new Node(v, hVal);

        }

        for (int v = 0; v < g.V(); v++) {
            // Create adjacent edges for each node
            Edge[] adjs = new Edge[g.degree(v)];
            int i = 0;
            for (int w : g.adj(v)) {
                adjs[i++] = new Edge(nodes[w], 1); 
            }
            nodes[v].adjacencies = adjs;
        }

        Node source = nodes[start];
        Node goal = nodes[end];

        //cost from start
        source.g_scores = 0;

        queue.add(source);

        boolean found = false;

        while((!queue.isEmpty())&&(!found)){

            //the node in having the lowest f_score value
            Node current = queue.poll();

            explored.add(current);

            //goal found
            if(current.value.equals(goal.value)){
                found = true;
            }

            //check every child of current node
            for(Edge e : current.adjacencies){
                Node child = e.target;
                double cost = e.cost;
                double temp_g_scores = current.g_scores + cost;
                double temp_f_scores = temp_g_scores + child.h_scores;


                /*if child node has been evaluated and 
                  the newer f_score is higher, skip*/

                if((explored.contains(child)) && 
                        (temp_f_scores >= child.f_scores)){
                    continue;
                }

                /*else if child node is not in queue or 
                  newer f_score is lower*/

                else if((!queue.contains(child)) || 
                        (temp_f_scores < child.f_scores)){

                    child.parent = current;
                    child.g_scores = temp_g_scores;
                    child.f_scores = temp_f_scores;

                    if(queue.contains(child)){
                        queue.remove(child);
                    }

                    queue.add(child);
                }

            }

        }

        List<Node> path = new ArrayList<Node>();

        for(Node node = goal; node!=null; node = node.parent){
            path.add(node);
        }

        Collections.reverse(path);

        return path;

    }

}

class Node{

    public final Integer value;
    public double g_scores;
    public final double h_scores;
    public double f_scores = 0;
    public Edge[] adjacencies;
    public Node parent;

    public Node(Integer val, double hVal){
        value = val;
        h_scores = hVal;
    }

    //public String toString(){
    //    return value;
    //}

}

class Edge{
    public final double cost;
    public final Node target;

    public Edge(Node targetNode, double costVal){
        target = targetNode;
        cost = costVal;
    }
}

