package structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;

/**
 * This class implements a directed graph. An undirected grapph can be implemented
 * by subclassing this class. Weights may be attached to the edges by subclassing the
 * Neighbor class accordingly.
 * 
 * @author Sesh Venugopal
 *
 * @param <T> The type of object used to represent vertices.
 */
public class DirGraph<T> {

	/**
	 * Array of Vertex instances (vertex info and adjacency list)
	 */
	public ArrayList<Vertex<T>> adjlists;    

	/**
	 * Array of vertices in maximal finding
	 */
	protected ArrayList<HashSet<Vertex<T>>> cliques;

	/*
	 * Used in clique algorithm.
	 */
	private ArrayList<Vertex<T>> bkPotential;
	private ArrayList<Vertex<T>> bkCandidates;
	private ArrayList<Vertex<T>> bkFound;

	public ArrayList<T> getShortestPath(int srcNumber, int dstNumber) {
		Vertex<T> sourceVertex = adjlists.get(srcNumber);
		Vertex<T> destinationVertex = adjlists.get(dstNumber);
		Vertex<T> currentVertex = null;

		ArrayList<T> shortestPath = new ArrayList<T>();
		Queue<Vertex<T>> q = new Queue<Vertex<T>>();
		q.enqueue(sourceVertex);

		HashMap<Vertex<T>, Boolean> visited = new HashMap<Vertex<T>, Boolean>();
		HashMap<Vertex<T>, Vertex<T>> previous = new HashMap<Vertex<T>, Vertex<T>>();
		visited.put(sourceVertex, true);

		while(!q.isEmpty()) {
			currentVertex = q.dequeue();

			if (currentVertex.equals(destinationVertex)) {
				break;
			} else {
				Neighbor nbr = currentVertex.neighbors.first();

				while (nbr != null) {
					if (!visited.containsKey(vertexFromNeighbor(nbr))) {
						q.enqueue(vertexFromNeighbor(nbr));
						visited.put(vertexFromNeighbor(nbr), true);
						previous.put(vertexFromNeighbor(nbr), currentVertex);
					}

					nbr = currentVertex.neighbors.next();
				}
			} 
		} if (!currentVertex.equals(destinationVertex)) {
			return null;
		} else for(Vertex<T> v = destinationVertex; v != null; v = previous.get(v)) {
			shortestPath.add(v.info);
		}

		Collections.reverse(shortestPath);
		return shortestPath;
	}

	private Vertex<T> vertexFromNeighbor(Neighbor nbr) {
		return this.adjlists.get(nbr.vertexNumber);
	}

	public ArrayList<ArrayList<T>> getCliques() {

		ArrayList<ArrayList<T>> cliqueInfo = new ArrayList<ArrayList<T>>();
		bkCandidates = new ArrayList<Vertex<T>>(adjlists);

		findCliques(bkPotential, bkCandidates, bkFound);

		int i = 0;
		for (HashSet<Vertex<T>> hs : cliques) {
			cliqueInfo.add(new ArrayList<T>());
			for (Vertex<T> v : hs) 
				cliqueInfo.get(i).add(v.info);
			i++;
		}

		return cliqueInfo;
	}

	private void findCliques(ArrayList<Vertex<T>> potential, ArrayList<Vertex<T>> candidates, ArrayList<Vertex<T>> found) {
		ArrayList<Vertex<T>> oCandidates = new ArrayList<Vertex<T>>(candidates);

		boolean notDone = false;

		for (int i = 0; i < found.size(); i++) {
			int edges = 0;

			for (int j = 0; j < candidates.size(); j++) 
				if (this.containsEdge(found.get(i), candidates.get(j))) 
					edges++;

			if (edges == candidates.size())
				notDone = true;
		} if (!notDone) {
			for (int i = 0; i < oCandidates.size(); i++) {
				ArrayList<Vertex<T>> newCandidates = new ArrayList<Vertex<T>>();
				ArrayList<Vertex<T>> newFound = new ArrayList<Vertex<T>>();

				potential.add(oCandidates.get(i));
				candidates.remove(oCandidates.get(i));

				for (int j = 0; j < candidates.size(); j++) 
					if (this.containsEdge(oCandidates.get(i), candidates.get(j)))
						newCandidates.add(candidates.get(j));

				for (int j = 0; j < found.size(); j++) 
					if (this.containsEdge(oCandidates.get(i), found.get(j)))
						newFound.add(found.get(j));

				if (newCandidates.isEmpty() && newFound.isEmpty()) {
					boolean already = false;

					for (HashSet<Vertex<T>> s : cliques)
						for (Vertex<T> v : potential)
							if (s.contains(v)) {
								s.addAll(potential); 
								already = true;
							}

					if (!already) cliques.add(new HashSet<Vertex<T>>(potential));

				} else findCliques(potential, newCandidates, newFound);

				found.add(oCandidates.get(i));
				potential.remove(oCandidates.get(i));
			}
		}
	}

	public int getNumberOfCliques() {
		findCliques(bkPotential, bkCandidates, bkFound);
		return cliques.size();
	}

	public boolean[] reachable(int u) {
		boolean[] b = new boolean[adjlists.size()];
		for(int i=0; i<adjlists.size(); i++){
			b[i] = reachable(u,i);
		}    
		return b;
	}

	public boolean reachable(int u, int v){
		if(u == v) return true;
		ArrayList<Integer> S = new ArrayList<Integer>();
		ArrayList<Integer> R = new ArrayList<Integer>();
		S.add(u);
		while(!S.isEmpty()){
			int x = S.remove(S.size()-1);
			if(!R.contains(x)){
				R.add(x);
				for(int i=0; i< adjlists.size(); i++){
					if(containsEdge(x,new Neighbor(i))){
						if(i==v) return true;
						S.add(i);
					}
				}
			}
		}

		return false;
	}

	public int getConnectedComponents() {
		int[] number = new int[adjlists.size()];
		for(int v = 0; v< adjlists.size(); v++){
			number[v] = 0;
		}
		int j = 0;
		for(int v = 0; v< adjlists.size(); v++){
			if(number[v]==0){
				j++;
				number[v]=j;
				ArrayList<Integer> S = new ArrayList<Integer>();
				ArrayList<Integer> R = new ArrayList<Integer>();
				S.add(v);
				while(!S.isEmpty()){
					int x = S.remove(S.size()-1);
					if(!R.contains(x)){
						R.add(x);
						for(int i=0; i< adjlists.size(); i++){
							if(containsEdge(x,new Neighbor(i))||containsEdge(i,new Neighbor(x))){
								number[i] = j;
								S.add(i);
							}
						}
					}
				}
			}
		}
		return j;
	}

	public UndirGraph<T> removeVertexFromGraph(int vrtxNum) {
		UndirGraph<T> subgraph = new UndirGraph<T>();
		int vCount = this.numberOfVertices();

		for (int i = 0; i < vCount; i++) {
			if (i != vrtxNum) {
				subgraph.addVertex(this.vertexInfoOf(i));
			}
		}

		vCount = subgraph.numberOfVertices();

		for (int i = 0; i < vCount; i++) {
			int firstRootRef = this.vertexNumberOf(subgraph.vertexInfoOf(i));

			for (int j = 0; j < vCount; j++) {
				int secondRootRef = this.vertexNumberOf(subgraph.vertexInfoOf(j));

				if (this.containsEdge(firstRootRef, new Neighbor(secondRootRef)))
					subgraph.addEdge(i, new Neighbor(j));
			}
		}

		return subgraph;
	}

	public String findConnectors(int i){

		String result= "None";
		UndirGraph<T> temp = removeVertexFromGraph(i);

		int click1 = getConnectedComponents();
		int click2 = temp.getConnectedComponents();

		if (click1 <click2) {
			result= adjlists.get(i).info.toString();
		}


		return result;
	}
	
	/**
	 * Initializes a new directed graph instance of default initial vertex capacity.
	 */
	public DirGraph() {
		adjlists = new ArrayList<Vertex<T>>();

		cliques = new ArrayList<HashSet<Vertex<T>>>();
		bkPotential = new ArrayList<Vertex<T>>();
		bkCandidates = new ArrayList<Vertex<T>>();
		bkFound = new ArrayList<Vertex<T>>();
	}

	/**
	 * Returns the number of vertices in this graph.
	 * 
	 * @return Number of vertices in this graph.
	 */
	public int numberOfVertices() {
		return adjlists.size();
	}

	/**
	 * Adds a vertex to this graph.
	 * 
	 * @param vertex Vertex to be added.
	 * @return Number assigned to this vertex in the graph.
	 */
	public int addVertex(T vertex) {
		if (!containsVertex(vertex)) {
			adjlists.add(new Vertex<T>(vertex));
		}
		return adjlists.size() - 1;
	}

	/**
	 * Tells whether this graph contains a given vertex or not.
	 * 
	 * @param vertex Vertex to be searched for in this graph.
	 * @return True if the given vertex is in this graph, false otherwise.
	 */
	public boolean containsVertex(T vertex) {
		return adjlists.indexOf(new Vertex<T>(vertex)) != -1;
	}

	/**
	 * Returns the internal vertex number for the given vertex.
	 * 
	 * @param vertex Vertex for which internal number is needed.
	 * @return Internal number assigned to the given vertex, -1 if the vertex is not in this graph.
	 */
	public int vertexNumberOf(T vertex) {
		return adjlists.indexOf(new Vertex<T>(vertex));
	}

	/**
	 * Returns the client-supplied vertex information associated with a given internal vertex number.
	 * 
	 * @param vertexNumber Internal vertex number.
	 * @return Associated client-supplied vertex information.
	 */
	public T vertexInfoOf(int vertexNumber) {
		Vertex<T> v = adjlists.get(vertexNumber);
		return v.info;
	}

	/**
	 * Tells whether there is an edge from a given vertex (internal number) to another (neighbor).
	 * 
	 * @param vertexNumber Internal number of vertex.
	 * @param nbr Neighbor to which edge is sought.
	 * @return True if there is an edge, false otherwise.
	 */
	public boolean containsEdge(int vertexNumber, Neighbor nbr) {
		Vertex<T> v = adjlists.get(vertexNumber);
		return v.neighbors.contains(nbr);
	}

	public boolean containsEdge(Vertex<T> v, Vertex<T> nbr) {
		return v.neighbors.contains(new Neighbor(this.vertexNumberOf(nbr.info)));
	}

	/**
	 * Adds an edge from a given vertex (internal number) to another (neighbor). Note: If
	 * this vertex already has an edge to this neighbor, this method will return without
	 * doing anything. In other words, multiple edges are not supported.
	 * 
	 * @param vertexNumber Internal number of vertex.
	 * @param nbr Neighbor to which edge is added.
	 */
	public void addEdge(int vertexNumber, Neighbor nbr) {
		Vertex<T> fromVertex = adjlists.get(vertexNumber);
		if (!fromVertex.neighbors.contains(nbr)) {
			fromVertex.neighbors.add(nbr);
		}
	}

	/**
	 * Returns the first neighbor of a given vertex.
	 * 
	 * @param vertexNumber Internal number of vertex.
	 * @return First neighbor of given vertex, null if there are no neighbors.
	 */
	public Neighbor firstNeighbor(int vertexNumber) {
		Vertex<T> v = adjlists.get(vertexNumber);
		return v.neighbors.first();
	}

	/**
	 * Returns the next neighbor of a given vertex.
	 * 
	 * @param vertexNumber Internal number of vertex.
	 * @return Next neighbor of given vertex, relative to an earlier call to
	 * 			first() or or next(); null if end of neighbors list is reached.
	 */
	public Neighbor nextNeighbor(int vertexNumber) {
		Vertex<T> v = adjlists.get(vertexNumber);
		return v.neighbors.next();
	}

	/**
	 * Clears this graph of all vertices and edges.
	 */
	public void clear() {
		adjlists.clear();
	}
}
