package reversionSort.module2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
	private static final int L = Integer.MIN_VALUE;
	private static final int R = Integer.MAX_VALUE;
	
	private static final boolean REALITY = false;
	private static final boolean DESIRE = true;
	
	public static void main(String[] args) {
		List<List<Integer>> data = FileHandler.read();
		for(List<Integer> l : data){
			int[] input = new int[l.size()];
			for(int i = 0; i<l.size(); i++){
				input[i] = l.get(i);
			}
			
			int[] terminals = init(input);
			System.out.println(arrayToString(terminals));
			
			Edge[] reality = new Edge[terminals.length];
			Edge[] desire = new Edge[terminals.length];
			
			Edge[][] edges = buildRealityDesire(terminals);
			
			reality = edges[0];
			desire = edges[1];
			System.out.println(edgesToString(reality,desire,terminals));
			
			List<Cycle> cycles = buildCycles(reality,desire,terminals);
			System.out.print(cyclesToString(cycles, terminals));
			
			List<Component> components = buildComponents(reality, cycles);
			System.out.print(componetsToString(components));
			System.out.println("----------------------------------------\n");
			
			// Here begins the sort
			// ###############################################################
			//while(hasGoodComponents(components)){
				for(Component c : components){
					if(c.isGood()){
						System.out.println("Considering the component with cycles: "+c.toString());
						
						for(Integer i : c.getElements()){
							Cycle cycle = cycles.get(i);
							
							if(cycle.getType() == Cycle.GOOD){
								System.out.println("Considering the GOOD cycle "+(cycle.getId()+1));
								
								for(int j = 0; j<cycle.getEdges().size()-1; j++){
									for(int k = j+1; k<cycle.getEdges().size(); k++){
										Edge aj = cycle.getEdges().get(j);
										Edge ak = cycle.getEdges().get(k);
										
										if((aj.isPositive() && !ak.isPositive()) || (!aj.isPositive() && ak.isPositive())){
											System.out.println("Considering the pair of edges " +singleEdgeToString(aj, terminals)
													+ " and " + singleEdgeToString(ak, terminals));
											
											// Fetch inner terms
											int idx_left = aj.isPositive() ? aj.getRight() : aj.getLeft();
											int idx_right = ak.isPositive() ? ak.getLeft() : ak.getRight();
											
											int[] nextList = reversal(terminals, idx_left, idx_right);
											
											System.out.println("Reversal: "+arrayToString(nextList));
										}
									}
								}
							}
						}
					}
				}
			//}
			// ###############################################################
		}
	}
	
	private static int[] reversal(int[] list, int idx_left, int idx_right) {
		int[] leftSegment = new int[idx_left];
		int[] rightSegment = new int[list.length-idx_right-1];
		int[] innerSegment = new int[list.length - (leftSegment.length + rightSegment.length)];
		
		for(int i = 0; i<list.length; i++){
			if(i < idx_left) leftSegment[i] = list[i];
			else if(i >= idx_left && i <= idx_right) innerSegment[innerSegment.length-1-(i-idx_left)] = list[i];
			else rightSegment[i-idx_right-1] = list[i];
		}
		
		return concat(leftSegment, concat(innerSegment, rightSegment));
	}
	
	private static int[] concat(int[] a, int[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   int[] c= new int[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}

	private static boolean hasGoodComponents(List<Component> components){
		for(Component c : components){
			if(c.isGood()) return true;
		}
		
		return false;
	}

	private static String componetsToString(List<Component> components) {
		String str = "\nComponents:\n";
		int idx = 0;
		for(Component c : components){
			idx++;
			
			str += "Component " + idx + " is ";  
			str += c.isGood() ? "Good" : "Bad";
			str += "\nCycles: ";
			
			for(Integer i : c.getElements()){
				str += (i+1) + " ";
			}
			str += "\n";
		}
		
		return str;
	}

	private static List<Component> buildComponents(Edge[] reality, List<Cycle> cycles) {
		int numCycles = cycles.size();
		List<Component> components = new ArrayList<Component>();
		for(int i = 0; i<numCycles; i++){
			Component c = new Component(i);
			c.setType(cycles.get(i).getType() == Cycle.GOOD ? Component.GOOD : Component.BAD);
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
							
							if(ai == ak && aj == al){
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

	private static int[] init(int[] input) {
		int[] result = new int[input.length*2+2];
		
		result[0] = L;
		result[result.length-1] = R;
		
		for(int i = 1; i<result.length-1; i++){
			int _i = i % 2 == 0 ? i : i+1;
			int idx = (_i/2)-1;
			result[i] = i % 2 == 0 ? input[idx] : -input[idx];
		}
		
		return result;
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
	
	private static int next(boolean[] visited) {
		for(int i = 0; i<visited.length; i++){
			if(!visited[i]) return i;
		}
		return -1;
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
	
	private static String arrayToString(int[] arg){
		String str = "";
		
		for(int i = 0; i<arg.length; i++){
			if(arg[i] == L) str += "L ";
			else if(arg[i] == R ) str += "R";
			else str += arg[i]+" ";
		}
		
		return str;
	}
	
	private static String edgesToString(Edge[] reality, Edge[] desire, int[] list) {
		String str = "Edges:\n";
		for(int i = 0; i<reality.length; i++){
			str += singleEdgeToString(reality[i], list)+"\n";
			str += singleEdgeToString(desire[i], list)+"\n";
		}
		
		return str;
	}
	
	private static String singleEdgeToString(Edge e, int[] list){
		String str = e.isPositive() ? "+" : "-";
		String a = mount(e.getLeft(), list);
		String b = mount(e.getRight(), list);
		str += "(" + a + "," + b + ")";
		
		return str;
	}
	
	private static String mount(int idx, int[] list) {
		int val = list[idx];
		if(val == L) return "L";
		else if(val == R) return "R";
		else return val + "";
	}

	private static String cyclesToString(List<Cycle> cycles, int[] list) {
		String str = "Cycles:\n";
		for(Cycle c : cycles){
			str += "Cycle " + (c.getId()+1) + " is ";  
			str += c.getType() == Cycle.GREAT ? "Great" : c.getType() == Cycle.GOOD ? "Good" : "Bad";
			str += "\n";
			
			for(Integer i : c.getVertexes()){
				if(list[i] == L) str += "L ";
				else if(list[i] == R) str += "R ";
				else str += list[i] + " ";
			}
			str += "\n";
		}
		
		return str;
	}
}
