package reversionSort.module2;

public class Edge {
	private int cycleId;
	private int left;
	private int right;
	
	public Edge(int left, int right) {
		this.left = left;
		this.right = right;
		this.cycleId = 0;
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

	public int getCycleId() {
		return cycleId;
	}

	public void setCycleId(int cycleId) {
		this.cycleId = cycleId;
	}
	
	@Override
	public String toString(){
		return "{cycleId:"+this.cycleId+",left:"+this.left+",right:"+this.right+"}";
	}
}
