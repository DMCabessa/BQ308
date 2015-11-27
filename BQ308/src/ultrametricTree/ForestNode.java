package ultrametricTree;

public class ForestNode{
	private int id;
	private ForestNode parent;
	
	public ForestNode(int id){
		this.id = id;
		this.parent = this;
	}
	
	public int getId(){
		return this.id;
	}

	public ForestNode findHeader(){
		if(this.parent != this) return this.parent.findHeader();
		else return this;
	}
	
	public ForestNode getParent(){
		return this.parent;
	}
	
	public void setParent(ForestNode e){
		this.parent = e;
	}

	// Reverse the order of pointers in the graph/tree
	public ForestNode reverse(ForestNode e) {
		if(parent == this){									// If this node has a free pointer
			parent = e;											// Reverse the direction of the pointers
			return this;										// Then return itself (now with a spare pointer)
		}else{												// If the parent of this node has a busy pointer
			parent.reverse(this);								// Reverse the pointers above and let go of the return (it is already linked)
			parent = e;											// Reverse the direction of the pointer
			return this;										// Then return itself (now with a spare pointer)
		}
	}
}
