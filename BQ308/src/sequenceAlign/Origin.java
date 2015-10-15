package sequenceAlign;

public class Origin {
	
	private char m;
	private int i;
	private int j;
	
	public Origin(char m, int i, int j) {
		super();
		this.m = m;
		this.i = i;
		this.j = j;
	}

	public char getM() {
		return m;
	}

	public void setM(char m) {
		this.m = m;
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}
	
	@Override
	public String toString(){
		return this.m+"("+this.i+","+this.j+")";
	}
}
