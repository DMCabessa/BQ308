package reversionSort.module1;

import java.util.List;

public class Cicle {
	public static final int GREAT = 1;
	public static final int GOOD = 0;
	public static final int BAD = -1;
	
	private int type;
	private List<Integer> vertexes;
	
	public Cicle(int type, List<Integer> vertexes){
		this.type = type;
		this.vertexes = vertexes;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<Integer> getVertexes() {
		return vertexes;
	}

	public void setVertexes(List<Integer> vertexes) {
		this.vertexes = vertexes;
	}
}
