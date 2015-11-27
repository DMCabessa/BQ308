package ultrametricTree;

import util.Util;

public class MST {
	private Edge[] edges;

	public MST(Edge[] edges) {
		this.edges = edges;
	}

	public Edge[] getEdges() {
		return edges;
	}
	
	@Override
	public String toString(){
		String result = "";
		for(Edge e : edges){
			result += Util.toChar(e.getA()) + " --(" + e.getWeight() + ")-- " + Util.toChar(e.getB()) + "\n"; 
		}
		
		return result;
	}
}
