package ultrametricTree;

import util.Util;

public class TreeNode{
	private Edge edge;
	private TreeNode leftChild;
	private TreeNode rightChild;
	private double leftHeight;
	private double rightHeight;
	
	private int CWindex;
	
	private static final int FOUND_A = -5;
	private static final int FOUND_B = -7;
	private static final int NOT_FOUND = -9;
	
	public TreeNode(Edge edge, TreeNode leftChild, TreeNode rightChild){
		this.edge = edge;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	public TreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(TreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public TreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(TreeNode rightChild) {
		this.rightChild = rightChild;
	}
	
	// DFS routine
	public boolean contains(int id){
		boolean found = false;
		if(this.edge.getA() == id || this.edge.getB() == id) return true;
		if(this.leftChild != null) found = this.leftChild.contains(id);
		if(this.rightChild != null) return found || this.rightChild.contains(id);
		return found;
	}

	public double getLeftHeight() {
		return leftHeight;
	}

	public void setLeftHeight(double leftHeight) {
		this.leftHeight = leftHeight;
	}

	public double getRightHeight() {
		return rightHeight;
	}
	
	// Get node overall height (since both sides are equal we choose left just because)
	public double getHeight(){
		if(leftChild != null) return this.leftHeight + leftChild.getHeight();
		else return this.leftHeight;
	}

	public void setRightHeight(double rightHeight) {
		this.rightHeight = rightHeight;
	}

	public int getCWindex() {
		return CWindex;
	}

	public void setCWindex(int cWindex) {
		CWindex = cWindex;
	}

	public void process(int a, int b, int i) {
		if(edge.getA() == a && edge.getB() == b) this.CWindex = i;
		else{
			if(leftChild != null) leftChild.process(a, b, i);
			if(rightChild != null) rightChild.process(a, b, i);
		}
	}

	public int lca(int a, int b) {
		int left = NOT_FOUND, right = NOT_FOUND;
		
		// Locate left data
		if(leftChild != null){
			left = leftChild.lca(a, b);
		}else{
			left = edge.getA() == a ? FOUND_A : edge.getA() == b ? FOUND_B : NOT_FOUND;
		}
		
		// Locate right data
		if(rightChild != null){
			right = rightChild.lca(a, b);
		}else{
			right = edge.getB() == a ? FOUND_A : edge.getB() == b ? FOUND_B : NOT_FOUND;
		}
		
		// Reasoning
		if(left >= 0) return left;
		else if(right >= 0) return right;
		else if(left != NOT_FOUND && right != NOT_FOUND && left != right) return CWindex;
		else if(left != NOT_FOUND) return left;
		else if(right != NOT_FOUND) return right;
		else return NOT_FOUND;
	}
	
	@Override
	public String toString(){
		String str = "(" + Util.toChar(edge.getA()) + ", " + Util.toChar(edge.getB()) + ") --> ";
		
		if(leftChild != null) str += "(" + Util.toChar(leftChild.getEdge().getA()) + ", " + Util.toChar(leftChild.getEdge().getB()) + ")";
		else str += Util.toChar(edge.getA());
		
		if(leftHeight > 0) str += "{" + leftHeight + "} ";
		else str += " ";
		
		if(rightChild != null) str += "(" + Util.toChar(rightChild.getEdge().getA()) + ", " + Util.toChar(rightChild.getEdge().getB()) + ")";
		else str += Util.toChar(edge.getB());
		
		if(rightHeight > 0) str += "{" + rightHeight + "}\n";
		else str += "\n";
		
		if(leftChild != null) str += leftChild.toString();
		if(rightChild != null) str += rightChild.toString();
		
		return str;
	}

	public double isUltrametric() {
		double left;
		double right;
		
		if(leftChild == null) left = leftHeight;
		else left = leftHeight + leftChild.isUltrametric();
		
		if(rightChild == null) right = rightHeight;
		else right = rightHeight + rightChild.isUltrametric();
		
		if(leftHeight > 0 && rightHeight > 0 && left == right) return left;
		else return -1;
	}
}
