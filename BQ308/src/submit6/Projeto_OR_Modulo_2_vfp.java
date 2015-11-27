package submit6;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Projeto_OR_Modulo_2_vfp {
	private static final int L = Integer.MIN_VALUE;
	private static final int R = Integer.MAX_VALUE;
	
	public static void main(String[] args) {
		List<List<Integer>> data = FileHandler.read();
		File f = new File("Projeto_OR_Modulo_2_vfp.out");
		
		for(List<Integer> l : data){
			
			int[] input = new int[l.size()];
			for(int i = 0; i<l.size(); i++){
				input[i] = l.get(i);
			}
			
			int[] terminals = init(input);
			FileHandler.writeln(arrayToString(terminals),f);
			
			Struct struct = Struct.init(terminals);
			
			Edge[] reality = struct.getReality();
			Edge[] desire = struct.getDesire();

			FileHandler.writeln(edgesToString(reality,desire,terminals),f);
			
			List<Cycle> cycles = struct.getCycles();
			FileHandler.write(cyclesToString(cycles, terminals),f);
			
			List<Component> components = struct.getComponents();
			FileHandler.write(componetsToString(components),f);
			
			// Here begins the sort
			// ###############################################################
			boolean hasGoodComponents = hasGoodComponents(components, cycles);
			Struct loopStruct = null;
			int[] loopTerminals = null;
			while(hasGoodComponents){
				FileHandler.writeln("\n=================================\n"
						+ "Begin reversal\n"
						+ "=================================",f);
				List<Component> loopComponents = loopStruct != null ? loopStruct.getComponents() : components;
				List<Cycle> loopCycles = loopStruct != null ? loopStruct.getCycles() : cycles;
				terminals = loopTerminals != null ? loopTerminals : terminals;
				
				for(Component c : loopComponents){
					if(c.isGood()){
						FileHandler.writeln("\nConsidering the component with cycles: "+c.toString(),f);
						
						for(Integer i : c.getElements()){
							Cycle cycle = loopCycles.get(i);
							
							if(cycle.getType() == Cycle.GOOD){
								FileHandler.writeln("Considering the GOOD cycle "+(cycle.getId()+1),f);
								
								for(int j = 0; j<cycle.getEdges().size()-1; j++){
									for(int k = j+1; k<cycle.getEdges().size(); k++){
										Edge aj = cycle.getEdges().get(j);
										Edge ak = cycle.getEdges().get(k);
										
										if((aj.isPositive() && !ak.isPositive()) || (!aj.isPositive() && ak.isPositive())){
											FileHandler.writeln("Considering the pair of edges " +singleEdgeToString(aj, terminals)
													+ " and " + singleEdgeToString(ak, terminals),f);
											
											// Fetch inner terms
											int idx_left = aj.isPositive() ? aj.getRight() : aj.getLeft();
											int idx_right = ak.isPositive() ? ak.getLeft() : ak.getRight();
											
											int[] nextList = reversal(terminals, idx_left, idx_right);
											Struct nextStruct = Struct.init(nextList);
											FileHandler.write("Reversal: "+arrayToString(nextList),f);
											
											if(loopStruct == null
													|| (nextStruct.getCycles().size() > loopStruct.getCycles().size()
															&& nextStruct.badComponents() <= loopStruct.badComponents())){
												loopStruct = nextStruct;
												loopTerminals = nextList;
												FileHandler.write(" *Last list selected",f);
											}
											FileHandler.writeln("",f);
											
											FileHandler.writeln("Number of cycles after reversion: "+nextStruct.getCycles().size(),f);
											FileHandler.writeln("Number of bad components after reversion: "+nextStruct.badComponents(),f);
										}
									}
								}
							}
						}
					}
				}
				
				hasGoodComponents = hasGoodComponents(loopStruct.getComponents(), loopStruct.getCycles());
			}
			
			if((loopStruct != null && loopStruct.badComponents() > 0) || struct.badComponents() > 0){
				FileHandler.writeln("\n\nIn presence of bad components...",f);
			}
			// ###############################################################
			
			FileHandler.writeln("----------------------------------------\n",f);
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

	private static boolean hasGoodComponents(List<Component> components, List<Cycle> cycles){
		for(Component c : components){
			if(c.isGood()){
				if(c.getElements().size() == 1){
					if(cycles.get((int) c.getElements().toArray()[0]).getType() == Cycle.GREAT){
						continue;
					}
				}
				return true;
			}
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
			boolean halt = false;
			if(c.getType() == Cycle.GOOD){
				str += " -> Divergent edges ";
				for(int i = 0; i<cycles.size()-1 && !halt; i++){
					for(int j = i+1; j<cycles.size() && !halt; j++){
						Edge ai = c.getEdges().get(i);
						Edge aj = c.getEdges().get(j);
						
						if((ai.isPositive() && !aj.isPositive()) || (!ai.isPositive() && aj.isPositive())){
							str += singleEdgeToString(ai, list) + " and ";
							str += singleEdgeToString(aj, list);
							halt = true;
						}
					}
				}
			}
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

class FileHandler {
	public final static File INPUT = new File("Projeto_OR_Modulo_2_vfp.in");
	
	public static void write(String content, File file){
		try{
			if (!file.exists()) file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(content);
			bw.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void writeln(String content, File file){
		try{
			if (!file.exists()) file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(content+"\n");
			bw.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static List<List<Integer>> read(){
		try {
			FileReader fr = new FileReader(INPUT);
			BufferedReader br = new BufferedReader(fr);
			List<List<Integer>> result = new ArrayList<List<Integer>>();
			
			while(true){
				int length = Integer.parseInt(br.readLine());
				if(length == 0) break;
				
				String line = br.readLine();
				String[] vals = line.split(" ");
				List<Integer> nums = new ArrayList<Integer>();
				for(String val : vals){
					nums.add(Integer.parseInt(val));
				}
				
				result.add(nums);
			}
			
			br.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

class Struct {
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

class Edge {
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

class Cycle {
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

class Component {
	public static final boolean GOOD = true;
	public static final boolean BAD = false;
	
	private boolean type;
	private Set<Integer> cycles;
	
	public Component(){
		this.cycles = new HashSet<Integer>();
	}
	
	public Component(int i){
		this.cycles = new HashSet<Integer>();
		this.cycles.add(i);
	}
	
	public void union(Component e){
		cycles.addAll(e.cycles);
	}
	
	public void add(Integer i){
		this.cycles.add(i);
	}
	
	public Set<Integer> getElements(){
		return this.cycles;
	}

	public boolean isGood() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}
	
	@Override
	public String toString(){
		String str = "{";
		int idx = 0;
		for(Integer i : cycles){
			idx++;
			str += (i+1);
			
			if(idx < cycles.size()) str += ",";
		}
		str += "}";
		
		return str;
	}
}
