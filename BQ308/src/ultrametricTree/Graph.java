package ultrametricTree;

import util.Util;

public class Graph {
	public int[][] ADJENCY_MATRIX;
	
	public Graph(int[][] matrix){
		this.ADJENCY_MATRIX = matrix;
	}

	public MST toMST() {
		// Build unique edge list
		Edge[] edges = new Edge[Util.terminal(ADJENCY_MATRIX.length-1)];
		int idx = 0;
		for (int i = 0; i < ADJENCY_MATRIX.length; i++) {
			for (int j = 0; j < i; j++) {
				edges[idx] = new Edge(i,j,ADJENCY_MATRIX[i][j]);
				idx++;
			}
		}
		
		// Run Kruskal to build tree
		Forest f = new Forest();
		
		for(int i = 0; i < ADJENCY_MATRIX.length; i++){
			f.MAKE_SET(i);
		}
		
		// Sort list of edges by weight
		QuickSort.ordenar(edges);
		for(Edge e : edges){
			ForestNode A = f.FIND_SET(e.getA());
			ForestNode B = f.FIND_SET(e.getB());
			@SuppressWarnings("unused")
			boolean a = true;
			if(A.getId() != B.getId()){
				f.UNION(e.getA(), e.getB());
			}
		}
		
		return f.toMST(this);
	}
}