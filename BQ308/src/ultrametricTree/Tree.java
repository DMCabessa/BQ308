package ultrametricTree;

import java.util.ArrayList;
import java.util.List;;

public class Tree {
	private TreeNode root;
	
	// Build auxiliary tree R
	public Tree(MST T, Graph Gh){
		Edge[] edges = T.getEdges();
		
		QuickSort.ordenar(edges);
		List<TreeNode> buffer = new ArrayList<TreeNode>();
		
		for(Edge e : edges){
			TreeNode ra = findSubTree(buffer, e.getA());
			TreeNode rb = findSubTree(buffer, e.getB());
			TreeNode r = new TreeNode(e, ra, rb);
			buffer.add(r);
		}
		
		this.root = buffer.get(0);
	}
	
	// Build ultrametric tree U
	public Tree(Tree R, MST T, int[] CW) {
		double height = 0;
		
		Edge[] t = T.getEdges();
		for(int i = 0; i<t.length; i++){
			t[i].setWeight(CW[i]);
		}
		
		QuickSort.ordenar(t);
		List<TreeNode> buffer = new ArrayList<TreeNode>();
		
		for(int i = 0; i<t.length; i++){
			Edge e = t[i];
			
			TreeNode ua = findSubTree(buffer, e.getA());
			TreeNode ub = findSubTree(buffer, e.getB());
			TreeNode u = new TreeNode(e, ua, ub);
			
			height = (double) t[i].getWeight()/2;
			u.setLeftHeight(height - (ua != null ? ua.getHeight() : 0));
			u.setRightHeight(height - (ub != null ? ub.getHeight() : 0));
			
			buffer.add(u);
		}
		
		this.root = buffer.get(0);
	}

	private TreeNode findSubTree(List<TreeNode> l, int id){
		for(TreeNode e : l){
			if(e.contains(id)) {
				l.remove(e);
				return e;
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return root.toString();
	}

	// Pre-process R for the lca queries
	public void process(MST t) {
		for(int i = 0; i<t.getEdges().length; i++){
			this.root.process(t.getEdges()[i].getA(), t.getEdges()[i].getB(), i);
		}
	}

	// Find the index of the lowest common ancestor between a and b
	public int lca(int a, int b) {
		return root.lca(a,b);
	}

	public boolean isUltrametric() {
		return root.isUltrametric() > 0;
	}
}
