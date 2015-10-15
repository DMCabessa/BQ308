package sequenceAlign;

public class Sequence{
	
	private String name;
	private String trace;
	
	public Sequence(String name, String trace) {
		super();
		this.name = name;
		this.trace = trace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTrace() {
		return trace;
	}

	public void setTrace(String trace) {
		this.trace = trace;
	}
	
}
