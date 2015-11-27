package reversionSort.module1;

public class Edge {
	private int left;
	private int right;
	
	public Edge(int left, int right) {
		this.left = left;
		this.right = right;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public boolean isPositive() {
		return left < right;
	}
	
}
