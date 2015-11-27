package reversionSort.module2;

import java.util.HashSet;
import java.util.Set;

public class Component {
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
