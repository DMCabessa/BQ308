package sequenceAlign;

public class Ordering {

	private String ordering;
	
	public Ordering(String ordering) {
		this.ordering = ordering;
	}
	
	public Ordering(){
		this.ordering = "SIR";
	}
	
	public boolean isPriorityOver(char a, char b){
		for(int i = 0; i<this.ordering.length(); i++){
			if(this.ordering.charAt(i) == a) return true;
			if(this.ordering.charAt(i) == b) return false;
		}
		return false;
	}
}
