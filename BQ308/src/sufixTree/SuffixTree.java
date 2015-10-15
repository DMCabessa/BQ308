package sufixTree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SuffixTree {
	private Node root;
	
	private SuffixTree(Node root){
		this.root = root;
	}
	
	public static SuffixTree makeTree(String X, PrintStream ps){
		Node root = new Node(0,0,0);					// Start root and first node
		root.addChild(new Node(1,1,1));					
		SuffixTree st = new SuffixTree(root);
		printTree(st.root, ps);							// Print first tree
		ps.println("--");
		int index = 2;
		
		for(int i = 2; i<=X.length(); i++){				// Build a ST (x1..xi)
			char xi = X.charAt(i-1);
			for(int j = 1; j<=i; j++){					// Create a path for the suffix X[j..i] 
				Node u;
				if(j == i) u = st.root;					// At the end of iterations, run for root
				else u = st.locus(j,i-1, st.root, X);		// Find the locus of X[j..i-1]
				
				Node n = u.next(xi, X);
				boolean breakpoint = true;
				if(n == null){							// If there is no xi after u
					if(!u.hasChilds()){					// If u is a leaf
						u.LENGTH += 1;					// Extend label of u as u+xi
					}else{
						// Insert a new node w = X[j..i] and a edge (u,w)
						u.addChild(new Node(index,j,u.LENGTH+1));
						index++;
					}
				}else if(n.LENGTH > 1){					// If there is xi, bifurcate path
					Node newChild = new Node(index,n.BEGIN,u.LENGTH+1);
					newChild.addChild(n);
					u.addChild(newChild);
					index++;
				}else{
					u.addChild(n);
				}
			}
			
			printTree(st.root, ps); 					// Print tree after each step	
			ps.println("--");
		}
		
		return st;
	}

	private static void printTree(Node e, PrintStream ps) {
		String origin = "ORIGEM: ";
		String target = "; DESTINO: ";
		String label = "; ROTULO: ";
		
		if(e.hasChilds()){
			for(Node node : e.getChilds()){
				ps.println(origin+e.ID+target+node.ID+label+node.BEGIN+","+node.LENGTH);
				printTree(node, ps);
			}
		}
	}

	private Node locus(int begin, int end, Node e, String str) {
		if(e.BEGIN > 0 && 
		str.substring(e.BEGIN-1, e.BEGIN+e.LENGTH-1).equals(str.substring(begin-1, end))) return e;
		else if(e.hasChilds()){
			List<Node> childs = e.getChilds();
			for (Node node : childs) {
				Node x = locus(begin,end,node,str);
				if(x != null) return x;
			}
		}else{
			return null;
		}
		return null;
	}
}

class Node{
	public int ID;
	public int BEGIN;
	public int LENGTH;
	private List<Node> childs;
	
	public Node(int id, int begin, int length){
		this.ID = id;
		this.BEGIN = begin;
		this.LENGTH = length;
		this.childs = new ArrayList<Node>();
	}
	
	public void addChild(Node e){
		this.childs.add(e);
	}

	public List<Node> getChilds() {
		return childs;
	}
	
	public boolean hasChilds(){
		return childs.size() > 0;
	}
	
	public Node next(char ch, String str){
		if(!this.hasChilds()) return null;
		else{
			for (Node node : childs) {
				if(str.charAt(node.BEGIN-1) == ch){
					Node acc = node;
					childs.remove(node);
					return acc;
				}
			}
		}
		return null;
	}
	
}