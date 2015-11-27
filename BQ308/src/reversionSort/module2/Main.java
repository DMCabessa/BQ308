package reversionSort.module2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
	private static final int L = Integer.MIN_VALUE;
	private static final int R = Integer.MAX_VALUE;
	
	public static void main(String[] args) {
		List<List<Integer>> data = FileHandler.read();
		for(List<Integer> l : data){
			int[] input = new int[l.size()];
			for(int i = 0; i<l.size(); i++){
				input[i] = l.get(i);
			}
			
			int[] terminals = init(input);
			System.out.println(arrayToString(terminals));
			
			Struct struct = Struct.init(terminals);
			
			Edge[] reality = struct.getReality();
			Edge[] desire = struct.getDesire();

			System.out.println(edgesToString(reality,desire,terminals));
			
			List<Cycle> cycles = struct.getCycles();
			System.out.print(cyclesToString(cycles, terminals));
			
			List<Component> components = struct.getComponents();
			System.out.print(componetsToString(components));
			
			// Here begins the sort
			// ###############################################################
			boolean hasGoodComponents = hasGoodComponents(components, cycles);
			Struct loopStruct = null;
			int[] loopTerminals = null;
			while(hasGoodComponents){
				System.out.println("\n=================================\n"
						+ "Begin reversal\n"
						+ "=================================");
				List<Component> loopComponents = loopStruct != null ? loopStruct.getComponents() : components;
				List<Cycle> loopCycles = loopStruct != null ? loopStruct.getCycles() : cycles;
				terminals = loopTerminals != null ? loopTerminals : terminals;
				
				for(Component c : loopComponents){
					if(c.isGood()){
						System.out.println("\nConsidering the component with cycles: "+c.toString());
						
						for(Integer i : c.getElements()){
							Cycle cycle = loopCycles.get(i);
							
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
											Struct nextStruct = Struct.init(nextList);
											System.out.print("Reversal: "+arrayToString(nextList));
											
											if(loopStruct == null
													|| (nextStruct.getCycles().size() > loopStruct.getCycles().size()
															&& nextStruct.badComponents() <= loopStruct.badComponents())){
												loopStruct = nextStruct;
												loopTerminals = nextList;
												System.out.print(" *Last list selected");
											}
											System.out.println();
											
											System.out.println("Number of cycles after reversion: "+nextStruct.getCycles().size());
											System.out.println("Number of bad components after reversion: "+nextStruct.badComponents());
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
				System.out.println("\n\nIn presence of bad components...");
			}
			// ###############################################################
			
			System.out.println("----------------------------------------\n");
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
