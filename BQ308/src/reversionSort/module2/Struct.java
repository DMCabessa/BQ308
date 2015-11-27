package reversionSort.module2;

import java.util.ArrayList;
import java.util.List;

public class Struct {
	private static final int L = Integer.MIN_VALUE;
	private static final int R = Integer.MAX_VALUE;
	private static final boolean REALITY = false;
	private static final boolean DESIRE = true;
	
	private Edge[] reality;
	private Edge[] desire;
	private List<Cycle> cycles;
	private List<Component> components;
	
	private Struct(Edge[] reEdges, Edge[] deEdges, List<Cycle> cList, List<Component> cList2){
		this.reality = reEdges;
		this.desire = deEdges;
		this.cycles = cList;
		this.components = cList2;
	}
	
	public static Struct init(int[] terminals){
		Edge[] reality = new Edge[terminals.length];
		Edge[] desire = new Edge[terminals.length];
		
		Edge[][] edges = buildRealityDesire(terminals);
		
		reality = edges[0];
		desire = edges[1];
		
		List<Cycle> cycles = buildCycles(reality,desire,terminals);
		
		List<Component> components = buildComponents(reality, cycles);
		
		return new Struct(reality, desire, cycles, components);
	}
	
	private static Edge[][] buildRealityDesire(int[] list) {
		boolean edge = REALITY;
		boolean finish = false;
		Edge[][] result = new Edge[2][list.length/2];
		Edge[] reality = new Edge[list.length/2];
		Edge[] desire = new Edge[list.length/2];
		int r_idx = 0, d_idx = 0, idx = 0;
		
		boolean[] visited = new boolean[list.length];
		
		while(!finish){
			if(visited[idx]) idx = next(visited);
			visited[idx] = true;
			
			if(edge == REALITY){
				int next = searchReality(list, idx);
				reality[r_idx] = new Edge(idx, next);
				idx = next;
				r_idx++;
				edge = DESIRE;
			}else{
				int next = searchDesired(list, idx); 
				desire[d_idx] = new Edge(idx, next);
				idx = next;
				d_idx++;
				edge = REALITY;
			}
			
			if(reality[reality.length-1] != null && desire[desire.length-1] != null){
				finish = true;
			}
		}
		
		result[0] = reality;
		result[1] = desire;
		return result;
	}
	
	private static int searchDesired(int[] list, int idx) {
		int currentVal = list[idx];
		int nextVal = currentVal < 0 ? ((-currentVal)-1) : (-(currentVal+1));
		if(currentVal == R) nextVal = (list.length-2)/2;
		else if(currentVal == -1) nextVal = L;
		else if(currentVal == (list.length-2)/2) nextVal = R;
		
		for(int i = 0; i<list.length; i++){
			if(list[i] == nextVal) return i;
		}
		
		return -1;
	}
	
	private static int searchReality(int[] list, int idx) {
		int currentVal = list[idx];
		int leftVal = idx > 0 ? list[idx-1] : -currentVal;
		int rightVal = idx < list.length-1 ? list[idx+1] : -currentVal;
		
		if(currentVal == -leftVal) return idx+1;
		else if(currentVal == -rightVal) return idx-1;
		else return -1;
	}
	
	private static int next(boolean[] visited) {
		for(int i = 0; i<visited.length; i++){
			if(!visited[i]) return i;
		}
		return -1;
	}
	
	private static List<Cycle> buildCycles(Edge[] reality, Edge[] desire, int[] list) {
		boolean[] visited = new boolean[list.length];
		List<Cycle> cycles = new ArrayList<Cycle>();
		int e_idx = 0;
		int id = 0;
		
		while(next(visited) >= 0){
			Cycle cycle = new Cycle();
			List<Edge> edges = new ArrayList<Edge>();
			
			List<Integer> vertexes = new ArrayList<Integer>();
			int idx = next(visited);
			vertexes.add(idx);
			visited[idx] = true;
			int type = 0;
			boolean finish = false, hasPositive = false, hasNegative = false;
			
			while(!finish){
				vertexes.add(reality[e_idx].getRight());
				visited[reality[e_idx].getRight()] = true;
				reality[e_idx].setCycleId(id);
				desire[e_idx].setCycleId(id);
				
				edges.add(reality[e_idx]);
				
				if(reality[e_idx].isPositive()) hasPositive = true;
				else hasNegative = true;
				if(desire[e_idx].getRight() != idx) {
					vertexes.add(desire[e_idx].getRight());
					visited[desire[e_idx].getRight()] = true;
				}
				else finish = true;
				e_idx++;
			}
			
			if(vertexes.size() == 2) type = Cycle.GREAT;
			else if(hasPositive && hasNegative) type = Cycle.GOOD;
			else type = Cycle.BAD;
			
			cycle.setId(id);
			cycle.setType(type);
			cycle.setVertexes(vertexes);
			cycle.setEdges(edges);
			cycles.add(cycle);
			id++;
		}
		
		return cycles;
	}
	
	private static List<Component> buildComponents(Edge[] reality, List<Cycle> cycles) {
		int numCycles = cycles.size();
		List<Component> components = new ArrayList<Component>();
		for(int i = 0; i<numCycles; i++){
			Component c = new Component(i);
			c.setType(cycles.get(i).getType() == Cycle.BAD ? Component.BAD : Component.GOOD);
			components.add(c);
		}
		
		Edge[] edges = new Edge[reality.length];
		for(int i = 0; i<edges.length; i++){
			for(Edge e : reality){
				if(e.getLeft() == (i*2) || e.getRight() == (i*2)) edges[i] = e;
			}
		}
		
		if(edges.length >= 4){
			for(int i = 0; i<edges.length-3; i++){
				for(int j = i+1; j<edges.length-2; j++){
					for(int k = j+1; k<edges.length-1; k++){
						for(int l = k+1; l<edges.length; l++){
							int ai = edges[i].getCycleId();
							int aj = edges[j].getCycleId();
							int ak = edges[k].getCycleId();
							int al = edges[l].getCycleId();
							
							if(ai == ak && aj == al && ai != aj){
								components.get(ai).union(components.get(aj));
								components.get(ai).setType(components.get(ai).isGood() || components.get(aj).isGood());
								components.set(aj, new Component());
							}
						}
					}
				}
			}
		}
		
		List<Component> filledComponents = new ArrayList<Component>();
		for(Component c : components){
			if(c.getElements().size() > 0) filledComponents.add(c);
		}
		
		return filledComponents;
	}

	public Edge[] getReality() {
		return reality;
	}

	public Edge[] getDesire() {
		return desire;
	}

	public List<Cycle> getCycles() {
		return cycles;
	}

	public List<Component> getComponents() {
		return components;
	}
	
	public int badComponents(){
		int badComponents = 0;
		for(Component c : this.components){
			if(!c.isGood()) badComponents++;
		}
		
		return badComponents;
	}
}
