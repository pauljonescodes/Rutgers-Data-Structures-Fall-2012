package structures;

/**
 * This class implements an undirected graph. Since an undirected graph is a special
 * kind of directed graph--an undirected edge is two directed edges in opposite
 * directions--it is implemented by subclassing DirGraph.
 * 
 * @author Sesh Venugopal
 *
 */
public class UndirGraph<T> extends DirGraph<T> {
	
	/**
	 * Initializes a new undirected graph instance of default initial vertex capacity.
	 */
	public UndirGraph() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see structures.graph.DirGraph#addEdge(int, structures.graph.Neighbor)
	 */
	public void addEdge(int vertexNumber, Neighbor nbr) {
		super.addEdge(vertexNumber, nbr);
		super.addEdge(nbr.vertexNumber, new Neighbor(vertexNumber));
	}
}
