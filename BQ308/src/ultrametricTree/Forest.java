package ultrametricTree;

import java.util.HashSet;
import java.util.Set;

public class Forest {
	private Set<ForestNode> forest;
	
	
	public Forest(){
		this.forest = new HashSet<ForestNode>();
	}
	
	public void MAKE_SET(int i){
		forest.add(new ForestNode(i));
	}
	
	public ForestNode FIND_SET(int i){
		for(ForestNode e : forest){
			if(e.getId() == i) return e.findHeader();
		}
		return null;
	}
	
	public void UNION(int u, int v){
		ForestNode a = null, b = null;
		for(ForestNode e : forest){
			if(e.getId() == u) a = e;
			else if(e.getId() == v) b = e;
		}
		
		// Find best way to join nodes
		if(a.getParent() == a) a.setParent(b);
		else if(b.getParent() == b) b.setParent(a);
		else{
			a = a.reverse(null);
			a.setParent(b);
		}
	}
	
	public Set<ForestNode> getForest(){
		return this.forest;
	}

	public MST toMST(Graph Gh) {
		Edge[] edges = new Edge[forest.size()-1];
		int idx = 0;
		for(ForestNode e : forest){
			if(e.getParent() != e){
				edges[idx] = new Edge(e.getId(), e.getParent().getId(), Gh.ADJENCY_MATRIX[e.getId()][e.getParent().getId()]);
				idx++;
			}
		}
		
		return new MST(edges);
	}
}
