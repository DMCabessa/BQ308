package ultrametricTree;

import util.Util;

public class Edge{
	private int idA;
	private int idB;
	private int weight;
	
	public Edge(int a, int b, int w){
		this.idA = a;
		this.idB = b;
		this.weight = w;
	}
	
	public int getA(){
		return this.idA;
	}
	
	public int getB(){
		return this.idB;
	}
	
	public int getWeight(){
		return this.weight;
	}
	
	public void setWeight(int weight){
		this.weight = weight;
	}
	
	@Override
	public String toString(){
		return "(" + Util.toChar(idA) + "," + Util.toChar(idB) + ")";
	}
}
