package reversionSort.module1;

import java.util.ArrayList;
import java.util.List;

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
			
			int[] list = init(input);
			System.out.println(arrayToString(list));
			
			Edge[] reality = new Edge[list.length];
			Edge[] desire = new Edge[list.length];
			
			Edge[][] edges = buildRealityDesire(list);
			
			reality = edges[0];
			desire = edges[1];
			System.out.println(edgestoString(reality,desire,list));
			
			List<Cicle> cicles = buildCicles(reality,desire,list);
			System.out.print(ciclestoString(cicles, list));
			
			System.out.println("----------------------------------------\n");
		}
	}

	private static List<Cicle> buildCicles(Edge[] reality, Edge[] desire, int[] list) {
		boolean[] visited = new boolean[list.length];
		List<Cicle> cicles = new ArrayList<Cicle>();
		int e_idx = 0;
		
		while(next(visited) >= 0){
			List<Integer> vertexes = new ArrayList<Integer>();
			int idx = next(visited);
			vertexes.add(idx);
			visited[idx] = true;
			int type;
			boolean finish = false, hasPositive = false, hasNegative = false;
			
			while(!finish){
				vertexes.add(reality[e_idx].getRight());
				visited[reality[e_idx].getRight()] = true;
				if(reality[e_idx].isPositive()) hasPositive = true;
				else hasNegative = true;
				if(desire[e_idx].getRight() != idx) {
					vertexes.add(desire[e_idx].getRight());
					visited[desire[e_idx].getRight()] = true;
				}
				else finish = true;
				e_idx++;
			}
			
			if(vertexes.size() == 2) type = Cicle.GREAT;
			else if(hasPositive && hasNegative) type = Cicle.GOOD;
			else type = Cicle.BAD;
			
			cicles.add(new Cicle(type, vertexes));
		}
		
		return cicles;
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
	
	private static String edgestoString(Edge[] reality, Edge[] desire, int[] list) {
		String str = "Edges:\n";
		for(int i = 0; i<reality.length; i++){
			str += reality[i].isPositive() ? "+" : "-";
			String a = mount(reality[i].getLeft(), list);
			String b = mount(reality[i].getRight(), list);
			str += "(" + a + "," + b + ")\n";
			
			str += desire[i].isPositive() ? "+" : "-";
			a = mount(desire[i].getLeft(), list);
			b = mount(desire[i].getRight(), list);
			str += "(" + a + "," + b + ")\n";
		}
		
		return str;
	}
	
	private static String mount(int idx, int[] list) {
		int val = list[idx];
		if(val == L) return "L";
		else if(val == R) return "R";
		else return val + "";
	}

	private static String ciclestoString(List<Cicle> cicles, int[] list) {
		int n = 1;
		String str = "Cicles:\n";
		for(Cicle c : cicles){
			str += "Cicle " + n + " is ";  
			str += c.getType() == Cicle.GREAT ? "Great" : c.getType() == Cicle.GOOD ? "Good" : "Bad";
			str += "\n";
			
			for(Integer i : c.getVertexes()){
				if(list[i] == L) str += "L ";
				else if(list[i] == R) str += "R ";
				else str += list[i] + " ";
			}
			str += "\n";
			
			n++;
		}
		
		return str;
	}
}
