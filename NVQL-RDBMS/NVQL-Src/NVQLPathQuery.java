import java.io.*;
import java.net.*;
import java.nio.*;
import java.sql.*;
import java.util.*;
import java.lang.*;
import java.util.Scanner;

class Node implements Comparator<Node> {

	// Member variables of this class
	public int node;
	public int cost;

	// Constructors of this class

	// Constructor 1
	public Node() {}

	// Constructor 2
	public Node(int node, int cost)
	{

		// This keyword refers to current instance itself
		this.node = node;
		this.cost = cost;
	}

	// Method 1
	@Override public int compare(Node node1, Node node2)
	{

		if (node1.cost < node2.cost)
			return -1;

		if (node1.cost > node2.cost)
			return 1;

		return 0;
	}
}


class ShortestPath{

	// Member variables of this class
	int dist[];
	int pred[];
	private Set<Integer> settled;
	private PriorityQueue<Node> pq;
	// Number of vertices
	private int V;
	List<List<Node> > adj;
	
	// Constructor of this class
	public ShortestPath(int V){
		// This keyword refers to current object itself
		this.V = V;
		dist = new int[V];
		pred = new int[V];
		settled = new HashSet<Integer>();
		pq = new PriorityQueue<Node>(V, new Node());
	}
	
	// Method 1
	// Dijkstra's Algorithm
	
	public void dijkstra(List<List<Node> > adj, int src){

		this.adj = adj;

		for (int i = 0; i < V; i++)
			dist[i] = Integer.MAX_VALUE;

		// Add source node to the priority queue
		pq.add(new Node(src, 0));

		// Distance to the source is 0
		dist[src] = 0;
		// Predecessor of the src is none
		pred[src] = src;

		while (settled.size() != V) {

			// Terminating condition check when
			// the priority queue is empty, return
			if (pq.isEmpty())
				return;

			// Removing the minimum distance node
			// from the priority queue
			int u = pq.remove().node;

			// Adding the node whose distance is
			// finalized
			if (settled.contains(u))

				// Continue keyword skips execution for
				// following check
				continue;

			// We don't have to call e_Neighbors(u)
			// if u is already present in the settled set.
			settled.add(u);

			e_Neighbours(u);
		}
	}
    
	private void e_Neighbours(int u){

		int edgeDistance = -1;
		int newDistance = -1;

		// All the neighbors of v
		for (int i = 0; i < adj.get(u).size(); i++) {
			Node v = adj.get(u).get(i);

			// If current node hasn't already been processed
			if (!settled.contains(v.node)) {
				edgeDistance = v.cost;
				newDistance = dist[u] + edgeDistance;

				// If new distance is cheaper in cost
				if (newDistance < dist[v.node]){
					dist[v.node] = newDistance;
					pred[v.node] = u;
				}

				// Add the current node to the queue
				pq.add(new Node(v.node, dist[v.node]));
			}
		}
	}


	public void printAllPaths(int src){
		// Printing the shortest path to all the nodes
		// from the source node
		System.out.println("The cost of shortest path from node :");
		for (int i = 0; i < dist.length; i++)
			if(i==src)
				System.out.println((src+1) + " to " + (i+1) + " is "
							+ dist[i] + " and predecessor is " + pred[i]);
			else
				System.out.println((src+1) + " to " + (i+1) + " is "
							+ dist[i] + " and predecessor is " + (pred[i]+1));
	}
	
	private void printPath(int src, int dest){
		if (dest!=src)
			printPath(src, pred[dest]);
		
		if (dest!=src)
			System.out.print(" --> " + (dest+1));
		else
			System.out.print("" + (dest+1));
	}

	public void printShortestPath(int src, int dest){
		System.out.println("Shortest path from "+ src + " to " + dest + ":");
		printPath(src-1, dest-1);
		System.out.println();
	}

	

	




    /*
	public static void main(String arg[])
	{

		int V = 5;
		int source = 0;

		// Adjacency list representation of the
		// connected edges by declaring List class object
		// Declaring object of type List<Node>
		List<List<Node> > adj
			= new ArrayList<List<Node> >();

		// Initialize list for every node
		for (int i = 0; i < V; i++) {
			List<Node> item = new ArrayList<Node>();
			adj.add(item);
		}

		// Inputs for the GFG(dpq) graph
		adj.get(0).add(new Node(1, 9));
		adj.get(0).add(new Node(2, 6));
		adj.get(0).add(new Node(3, 5));
		adj.get(0).add(new Node(4, 3));

		adj.get(2).add(new Node(1, 2));
		adj.get(2).add(new Node(3, 4));

		// Calculating the single source shortest path
		ShortestPath dpq = new ShortestPath(V);
		dpq.dijkstra(adj, source);

		// Printing the shortest path to all the nodes
		// from the source node
		System.out.println("The shorted path from node :");

		for (int i = 0; i < dpq.dist.length; i++)
			System.out.println(source + " to " + i + " is "
							+ dpq.dist[i]);
	}
    */
}








public class NVQLPathQuery{

    public static void main(String[] args) throws Exception {
        
        String pgsqlHost="localhost";
        String pgsqlPort="5432";
        String pgsqlDBName="NVQL";
        String pgsqlUserName="postgres";
        String pgsqlPassword="abcdefgh";
        Connection pgsqlCon=null;
        long startTime, endTime;
        NVQLQueryExecTime timeobj = new NVQLQueryExecTime();
        
            
        System.out.println("Starting...");       
        try{
            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://" + pgsqlHost + ":" + pgsqlPort + "/" + pgsqlDBName;

            Class.forName(driver);
            pgsqlCon = DriverManager.getConnection(url, pgsqlUserName, pgsqlPassword);
            System.out.println("PostgreSQL Connection Established");
        }catch (Exception e){
            System.out.println("PostgreSQL Connection Failed");
            System.out.println(e);
            System.exit(0);
        }

        //startTime=System.currentTimeMillis();
        

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter source IP address: ");
        String srcIP = scanner.nextLine();
        System.out.println("Source IP: " + srcIP);

        String query = "select \"host_Id\" from host as p where p.\"ipAddr\" = '" + srcIP +"'";
        ResultSet result = null;
        try {
            PreparedStatement pstmt = pgsqlCon.prepareStatement(query);
            
            result = pstmt.executeQuery();
            System.out.println("Get Source Host Id: Success");
        } catch (Exception e) {
            System.out.println("Get Source Host Id: Failure");
            System.out.println(e);
            System.exit(0);
        }

        String srcHostId = null;
        while(result.next())
            srcHostId = result.getString(1);

        System.out.println("Source Host Id = " + srcHostId);
        
        System.out.print("Enter destination IP address: ");
        String dstIP = scanner.nextLine();
        System.out.println("Destination IP: " + dstIP);

        query = "select \"host_Id\" from host as p where p.\"ipAddr\" = '" + dstIP +"'";
        result = null;
        try {
            PreparedStatement pstmt = pgsqlCon.prepareStatement(query);
            
            result = pstmt.executeQuery();
            System.out.println("Get Destination Host Id: Success");
        } catch (Exception e) {
            System.out.println("Get Destination Host Id: Failure");
            System.out.println(e);
            System.exit(0);
        }

        String dstHostId = null;
        while(result.next())
            dstHostId = result.getString(1);

        System.out.println("Destination Host Id = " + dstHostId);

		
		query = "select count(*) from host";
        result = null;
        try {
            PreparedStatement pstmt = pgsqlCon.prepareStatement(query);
            
            result = pstmt.executeQuery();
            System.out.println("Get Destination Host Id: Success");
        } catch (Exception e) {
            System.out.println("Get Destination Host Id: Failure");
            System.out.println(e);
            System.exit(0);
        }

		int totalNoOfNodes=0;

		while(result.next())
            totalNoOfNodes = Integer.parseInt(result.getString(1));



        pgsqlCon.close();

        //Connect to TVA database

        startTime=System.nanoTime();

        System.out.println("Starting...");       
        try{
            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://" + pgsqlHost + ":" + pgsqlPort + "/" + "TVA";

            Class.forName(driver);
            pgsqlCon = DriverManager.getConnection(url, pgsqlUserName, pgsqlPassword);
            System.out.println("PostgreSQL Connection Established");
        }catch (Exception e){
            System.out.println("PostgreSQL Connection Failed");
            System.out.println(e);
            System.exit(0);
        }

        System.out.println("Starting from Host: " + srcHostId);

        
        int V = totalNoOfNodes; //No. of nodes
		int source = Integer.parseInt(srcHostId); //Source node

		// Adjacency list representation of the
		// connected edges by declaring List class object
		// Declaring object of type List<Node>
		List<List<Node> > adj = new ArrayList<List<Node> >();

		// Initialize list for every node
		for (int i = 0; i < totalNoOfNodes; i++){
			List<Node> item = new ArrayList<Node>();
			adj.add(item);
		}

        //Building the adjacency list

        for (int i = 0; i < totalNoOfNodes; i++){
            //Finding adjacent nodes of node i 

            query = "select distinct qec.\"host_id\", qec.\"cond\" from qce " +
                    "left join qec " +
                    "on qce.\"src_id\" = qec.\"src_id\" and qce.\"dst_id\" = qec.\"dst_id\" and qce.\"vulnerability\" = qec.\"vulnerability\" " +
                    "where qce.\"host_id\" = " + String.valueOf(i+1);

            result = null;
            try{
                PreparedStatement pstmt = pgsqlCon.prepareStatement(query);
                result = pstmt.executeQuery();
                System.out.println("Find Next Hop: Success");
            } catch (Exception e){
                System.out.println("Find Next Hop: Failure");
                System.out.println(e);
                System.exit(0);
            }

            System.out.print("Neighbors of node " + (i+1) + ": ");

            //String[] nextHopHostIds = new String[totalNoOfNodes];
           
            while(result.next()){
                int nextHopHostId = Integer.parseInt(result.getString(1));
                if(nextHopHostId!=i+1){
                    adj.get(i).add(new Node(nextHopHostId-1, 1));
                    System.out.print(nextHopHostId + " ");
                }
            }
            System.out.println();
        }

		int src = Integer.parseInt(srcHostId);
		int dst = Integer.parseInt(dstHostId);
        // Calculating the single source shortest path
		ShortestPath dpq = new ShortestPath(totalNoOfNodes);
		dpq.dijkstra(adj, src-1);
		//dpq.printAllPaths(Integer.parseInt(srcHostId)-1);
		//dpq.printShortestPath(Integer.parseInt(srcHostId)-1, Integer.parseInt(dstHostId)-1);

		int pathLengthSrc2Dst=0;

		System.out.println("The cost of shortest path from node :");
		for (int i = 0; i < dpq.dist.length; i++)
			if(i==src)
				System.out.println((src) + " to " + (i+1) + " is "
							+ dpq.dist[i] + " and predecessor is " + dpq.pred[i]);
			else{
				System.out.println((src) + " to " + (i+1) + " is "
							+ dpq.dist[i] + " and predecessor is " + (dpq.pred[i]+1));
				if(i==(dst-1))
					pathLengthSrc2Dst=dpq.dist[i];
			}
		
		if(dpq.dist[dst-1]==Integer.MAX_VALUE){
			System.out.println("Path from "+ src + " to " + dst + " doest not exist");
			System.exit(0);
		}
		int nodesInPath[] = new int[pathLengthSrc2Dst+1];

		nodesInPath[0]=src-1;
		nodesInPath[pathLengthSrc2Dst]=dst-1;
		
		int tempDst=dst-1;
		int predId;

		for(int i = pathLengthSrc2Dst-1; i > 0; i--){
			predId = dpq.pred[tempDst];
			nodesInPath[i]=predId;
			tempDst=predId;
		}

		System.out.println("Nodes in path from " + src + " to " + dst + ": ");
		//System.out.print((nodesInPath[0]+1));
		for(int i = 0; i < pathLengthSrc2Dst; i++){
			System.out.print((nodesInPath[i]+1)+ "-->");
		}
		System.out.print((nodesInPath[pathLengthSrc2Dst]+1));
		System.out.println();
        //endTime=System.currentTimeMillis();
        endTime=System.nanoTime();
        //System.out.println(startTime + " " + endTime);
        timeobj.displayTime(startTime,endTime);
    }
}