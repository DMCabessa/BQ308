package sequenceAlign;

public class Costs {
	
	private int match;
	private int substitution;
	private int insert;
	private int remove;
	private String ordering;
	
	public Costs() {
		this.match = 5;
		this.substitution = -1;
		this.insert = -8;
		this.remove = -6;
		this.ordering = "MSRI";
	}
	
	public Costs(int match, int substitution, int insert, int remove){
		this.match = match;
		this.substitution = substitution;
		this.insert = insert;
		this.remove = remove;
	}

	public int getMatch() {
		return match;
	}

	public void setMatch(int match) {
		this.match = match;
	}

	public int getSubstitution() {
		return substitution;
	}

	public void setSubstitution(int substitution) {
		this.substitution = substitution;
	}

	public int getInsert() {
		return insert;
	}

	public void setInsert(int insert) {
		this.insert = insert;
	}

	public int getRemove() {
		return remove;
	}

	public void setRemove(int remove) {
		this.remove = remove;
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public boolean getOrdering(char c, char d) {
		for(int i = 0; i<this.ordering.length(); i++){
			if(this.ordering.charAt(i) == c) return true;
			if(this.ordering.charAt(i) == d) return false;
		}
		return false;
	}

}
