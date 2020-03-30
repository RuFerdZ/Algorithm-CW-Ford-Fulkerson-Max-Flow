package master;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
/*
    -Author : Rusiru Fernando
    -UoW ID : w1714943
    -Date : 26/03/2020
    -Support Sources : http://www.cs.ucf.edu/~dmarino/progcontests/cop4516/samplecode/FordFulkerson.java
                       https://westminster.hosted.panopto.com/Panopto/Pages/Viewer.aspx?id=5c4fcc71-c8ff-4807-abae-ab85005fca51
                       https://www.youtube.com/watch?v=Iwc3Uj4aaF4
                       https://www.youtube.com/watch?v=3LG-My_MoWc
                       https://www.youtube.com/watch?v=Tl90tNtKvxs
*/
public class MaxFlow {

    private int nodes;                                                // This holds the number of nodes in the graph
    private int edges;                                                // This holds the number of edges in the graph
    private int[][] graph;                                            // This holds the original graph read from the text file
    final public static int maxIntVal = Integer.MAX_VALUE;            // A constant which stores the highest integer in order to compare the minimum flow at the first DFS
    private int[][] solution;                                         // This graph holds the final solution graph
    Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        MaxFlow flow = new MaxFlow();

        while (true) {
            File file = new File(flow.menu());                                                    // Loads the user desired text file
            flow.makeGraph(file);                                                                 // calls the makeGraph() method passing the text file as a parameter

            flow.solution = new int[flow.nodes][flow.nodes];                                      // This initializes the solution 2D array
            for (int row = 0; row < flow.nodes; row++) {                                          // This sets all flows of solution graph to 0
                for (int col = 0; col < flow.nodes; col++) {
                    flow.solution[row][col] = 0;
                }
            }


            System.out.println("Input Graph: " + Arrays.deepToString(flow.graph));                      // This prints the Input flow network
            System.out.println("Number of nodes: " + flow.nodes);                                       // prints number i=of nodes
            System.out.println("Number of edges: " + flow.edges);                                       // prints number of edges


            long startTime = System.nanoTime();                              // Find start time in milliseconds
            int maxflow = flow.addFlows();                                   // This invokes the maximum flow finding methods
            long endTime = System.nanoTime();                                // Find end time in milliseconds

            flow.finalizeSolution();                                         //this finalizes the solution graph flow values

            System.out.println("------------------------------------------------");
            System.out.println("Max flow of the network: " + maxflow);                                        // This prints the Maximum flow from source to sink
            System.out.println("Solution Graph : " + Arrays.deepToString(flow.solution));                     // This prints the solution graph
            System.out.println("Execution Time: " + ((endTime - startTime) / 1000000.0) + "ms");              // Execution time in milliseconds
            System.out.println("------------------------------------------------");

            System.out.print("press any key to exit, or 'y' to re-run: ");
            String choice = flow.input.next();
            if (choice.equalsIgnoreCase("y")){
                continue;
            }else {
                System.out.println("Thank You, Now Exiting...");
                break;
            }
        }
    }

    public String menu(){                                                                         // The user can choose which dataset to be loaded to find the max flow
        System.out.println("--------------------------");
        System.out.println("    MAX FLOW FINDER");
        System.out.println("--------------------------");
        System.out.println("1 : Load Network 1 ");
        System.out.println("2 : Load Network 2 ");
        System.out.println("3 : Load Network 3 ");
        System.out.println("4 : Load Network 4 ");
        System.out.println("5 : Exit ");
        System.out.println("--------------------------");
        System.out.print("Enter Choice: ");
        String choice = input.next();                                                             // Ask user choice
        System.out.println("--------------------------");
        String file = " ";
        outer: while (true){
            switch (choice){
                case "1":
                    file = "Networks/Network_1.txt";                                               // set path for network 1 as a string
                    break outer;
                case "2":
                    file = "Networks/Network_2.txt";                                               // set path for network 2 as a string
                    break outer;
                case "3":
                    file = "Networks/Network_3.txt";                                               // set path for network 3 as a string
                    break outer;
                case "4":
                    file = "Networks/Network_4.txt";                                               // set path for network 4 as a string
                    break outer;
                case "5":
                    System.out.println("Thank You, Now Exiting...");
                    System.exit(0);                                                         // Exit Program
                default:
                    System.out.print("Invalid Choice! Please Enter again: ");
                    choice = input.next();
                    System.out.println("--------------------------");
                    continue;
            }
        }
        return file;

    }


    public void makeGraph(File file) throws FileNotFoundException {  // This method reads the number of nodes and the graph from a text file and assign it to a 2D array
        Scanner sc = new Scanner(file);                               // Load the text file to the Scanner
        this.nodes = Integer.parseInt(sc.nextLine());                 // First line of the text file contains the number of nodes in the graph
        this.graph = new int[nodes][nodes];                           // An array of the size of nodes is Initialised
        int nodeCount = 0;
        edges = 0;
        while (sc.hasNext()) {                                        // The graph from the text file is loaded into the 2D graph
            int edgeCount=0;                                          // The index of both inner and outer arrays are the node numbers
            String nodeFlows = sc.nextLine();                         // This reads each "nodeCount" number of lines one by one to the variable
            Scanner in = new Scanner(nodeFlows);                      // It loads each line from the file to the Scanner
            while (in.hasNext()) {                                    // While all the "node" number of flow capacities, loop.
                graph[nodeCount][edgeCount] = in.nextInt();           // Insert flow capacities to 2D array
                if (graph[nodeCount][edgeCount]>0){                   // This checks if there is an edge between 2 nodes
                    edges++;                                          // Counts the number of edges in the network
                }
                edgeCount++;                                          // go to next node horizontally
            }
            nodeCount++;                                              // go to next node vertically
        }

    }


    public int addFlows() {                                                            //This method will sum up all the flows in augmented paths and return the max flow
        boolean[] visited = new boolean[nodes];                                        // This array tracks which node has been visited and which has not
        int flow = 0;                                                                  //This holds the max flow of the network
        while (true){                                                                  // Loop until no augmenting paths found.
            Arrays.fill(visited, false);                                           // This fills all the indexes of the visited array as false, i.e set all nodes as un-visited
            int augmentedPathFlowValue = depthFirstSearch(0, visited, maxIntVal);          // Run DFS ( 0 means source node index in the graph) and return the flow value for that path
            if (augmentedPathFlowValue == 0){                                          // If their is no more augmented paths the DFS method will return 0
                break;                                                                 // Therefore the method terminates
            }
            flow += augmentedPathFlowValue;                                            // add path flow to final flow if their is an augmented path
        }

        return flow;                                                                    // Return final total flow when the method is been terminated
    }

    public int depthFirstSearch(int currentNode, boolean[] visited, int bottleneckCapacity) {          // This method does breadth first searches to find augmented paths in the network and return the bottleneck capacity for that augmented path

        if (currentNode == nodes-1){                                                      //checks whether the current node is the Sink node, i.e whether source to sink path is there
            visited[currentNode] = true;
            return bottleneckCapacity;                                                    // If so, return the bottleneck capacity for that augmented path
        }

        if (visited[currentNode]){                                                        // Checks whether the current node has already been visited
            return 0;                                                                     // If so, that means there will be no flow from source to sink
        }

        visited[currentNode] = true;                                                      // Set current node as visited
        int flow = 0;



        for (int nodeTracker = 0; nodeTracker < nodes; nodeTracker++) {                   // Loop through all possible nodes.
            if (graph[currentNode][nodeTracker] > 0)                                                                             // Checks whether there is an flow from current node to another node
                flow = depthFirstSearch(nodeTracker, visited, Math.min(graph[currentNode][nodeTracker], bottleneckCapacity));    // If so, then recurse the DFS method for that node until it finds an augmented path or no path, and then return its flow value(bottleneck capacity)
                                                                                                                                // also finds the actual bottleneck capacity, by finding minimum capacity

            if (flow > 0) {                                       // If flow capacity greater than 0 means, there is a flow from source to sink
                graph[currentNode][nodeTracker] -= flow;          // Change the edge value, i.e reduce the bottleneck capacity from (forward)edge capacity
                graph[nodeTracker][currentNode] += flow;          // Change residual capacity for that edge, i.e add the bottleneck capacity for the back-flow capacity
                solution[currentNode][nodeTracker] +=flow;        // this updates the flows in the solution graph
                return flow;                                      // return the flow value between 2 nodes
            }
        }
        return 0;                                                 //return 0 if there was no augmented path
    }

    public void finalizeSolution(){                                                                     // This method evaluates final flow (difference between back-flow and forward-flow) of an edge
        for (int currentNode = 0 ; currentNode < nodes ; currentNode++){                                // Produces a distributed flow solution graph
            for (int nodeTracker = 0 ; nodeTracker < nodes ; nodeTracker++){
                if ((solution[currentNode][nodeTracker]>0) && (solution[nodeTracker][currentNode]>0)){
                    if(solution[currentNode][nodeTracker]>solution[nodeTracker][currentNode]){
                        solution[currentNode][nodeTracker] -= solution[nodeTracker][currentNode];
                        solution[nodeTracker][currentNode]=0;
                    }else if (solution[nodeTracker][currentNode]>solution[currentNode][nodeTracker]){
                        solution[nodeTracker][currentNode] -= solution[currentNode][nodeTracker];
                        solution[currentNode][nodeTracker]= 0;
                    }else{
                        solution[nodeTracker][currentNode] = 0;
                        solution[currentNode][nodeTracker] = 0;
                    }
                }
            }
        }
    }
}
