package reversionSort.module2;

import java.util.List;

public class Cycle {
	public static final int GREAT = 1;
	public static final int GOOD = 0;
	public static final int BAD = -1;
	
	private int id;
	private int type;
	private List<Integer> vertexes;
	private List<Edge> edges;
	
	public Cycle(){}
	
	public Cycle(int id, int type, List<Integer> vertexes){
		this.id = id;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}
}
