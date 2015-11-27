package submit4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class vfp_ProjetoUT {
	
	public static void main(String[] args) {
		int[][][] data = FileHandler.read(FileHandler.INPUT);
		//int[][][] data = FileHandler.read(new File("success=1&fail=3&iter=4.txt"));
		int[][] Ml = data[0];
		int[][] Mh = data[1];
		
		Graph Gh = new Graph(Mh);
		MST T = Gh.toMST();
		System.out.println("Minimum Spawning Tree T");
		System.out.println(T.toString());
		
		Tree R = new Tree(T, Gh);
		System.out.println("Auxiliary tree R");
		System.out.println(R.toString());
		
		// Pre-process R, adding the indexes of T in each node
		R.process(T);
		
		for (int i = 0; i < Mh.length; i++) {
			for (int j = 0; j < i; j++) {
				int e = lca(R, i, j);
				System.out.print("Lowest common ancestor (" + Util.toChar(i) + "," + Util.toChar(j) + ") --> ");
				System.out.println("(" + Util.toChar(T.getEdges()[e].getA()) + "," + Util.toChar(T.getEdges()[e].getB()) + ")");
			}
		}
		System.out.println();
		
		int[] CW = cutWeight(Ml, T, R);
		System.out.println("Cut-Weight array");
		System.out.println(arrayToString(CW));
		
		Tree U = new Tree(R, T, CW);
		System.out.println("Ultrametric tree U");
		System.out.println(U.toString());
		
		System.out.println("Is ultrametric: "+U.isUltrametric());
	}
	
	public static boolean isUltrametric(File f){
		int[][][] data = FileHandler.read(f);
		int[][] Ml = data[0];
		int[][] Mh = data[1];
		
		Graph Gh = new Graph(Mh);
		MST T = Gh.toMST();
//		System.out.println("Minimum Spawning Tree T");
//		System.out.println(T.toString());
		
		Tree R = new Tree(T, Gh);
//		System.out.println("Auxiliary tree R");
//		System.out.println(R.toString());
		
		// Pre-process R, adding the indexes of T in each node
		R.process(T);
		
//		for (int i = 0; i < Mh.length; i++) {
//			for (int j = 0; j < i; j++) {
//				int e = lca(R, i, j);
//				System.out.print("Lowest common ancestor (" + Util.toChar(i) + "," + Util.toChar(j) + ") --> ");
//				System.out.println("(" + Util.toChar(T.getEdges()[e].getA()) + "," + Util.toChar(T.getEdges()[e].getB()) + ")");
//			}
//		}
//		System.out.println();
		
		int[] CW = cutWeight(Ml, T, R);
//		System.out.println("Cut-Weight array");
//		System.out.println(arrayToString(CW));
		
		Tree U = new Tree(R, T, CW);
//		System.out.println("Ultrametric tree U");
//		System.out.println(U.toString());
		
		return U.isUltrametric();
	}

	private static String arrayToString(int[] CW) {
		String result = "[ ";
		
		for(int i : CW){
			result += i + " ";
		}
		
		result += "]\n";
		return result;
	}

	private static int[] cutWeight(int[][] Ml, MST T, Tree R) {
		int[] CW = new int[T.getEdges().length];
		
		for(int a = 0; a<Ml.length; a++){
			for(int b = 0; b<a; b++){
				// For each pair of objects
				int e = lca(R,a,b);
				if(Ml[a][b] > CW[e]){
					CW[e] = Ml[a][b];
				}
			}
		}
		
		return CW;
	}
	
	private static int lca(Tree R, int a, int b){
		return R.lca(a,b);
	}
}

class FileHandler {
	public final static File INPUT = new File("input.txt");
	
	public static void write(String content, File file){
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
	
	public static int[][][] read(File f){
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			int lines = Integer.parseInt(br.readLine());
			int[][][] m = new int[2][lines][lines];
			for(int i = 0; i < lines; i++){
				String[] vals = br.readLine().split(" ");
				for(int j = 0; j<lines; j++){
					m[0][i][j] = Integer.parseInt(vals[j]);
				}
			}
			for(int i = 0; i < lines; i++){
				String[] vals = br.readLine().split(" ");
				for(int j = 0; j<lines; j++){
					m[1][i][j] = Integer.parseInt(vals[j]);
				}
			}
			
			br.close();
			
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

class Graph {
	public int[][] ADJENCY_MATRIX;
	
	public Graph(int[][] matrix){
		this.ADJENCY_MATRIX = matrix;
	}

	public MST toMST() {
		// Build unique edge list
		Edge[] edges = new Edge[Util.terminal(ADJENCY_MATRIX.length-1)];
		int idx = 0;
		for (int i = 0; i < ADJENCY_MATRIX.length; i++) {
			for (int j = 0; j < i; j++) {
				edges[idx] = new Edge(i,j,ADJENCY_MATRIX[i][j]);
				idx++;
			}
		}
		
		// Run Kruskal to build tree
		Forest f = new Forest();
		
		for(int i = 0; i < ADJENCY_MATRIX.length; i++){
			f.MAKE_SET(i);
		}
		
		// Sort list of edges by weight
		QuickSort.ordenar(edges);
		for(Edge e : edges){
			ForestNode A = f.FIND_SET(e.getA());
			ForestNode B = f.FIND_SET(e.getB());
			@SuppressWarnings("unused")
			boolean a = true;
			if(A.getId() != B.getId()){
				f.UNION(e.getA(), e.getB());
			}
		}
		
		return f.toMST(this);
	}
}

class Tree {
	private TreeNode root;
	
	// Build auxiliary tree R
	public Tree(MST T, Graph Gh){
		Edge[] edges = T.getEdges();
		
		QuickSort.ordenar(edges);
		List<TreeNode> buffer = new ArrayList<TreeNode>();
		
		for(Edge e : edges){
			TreeNode ra = findSubTree(buffer, e.getA());
			TreeNode rb = findSubTree(buffer, e.getB());
			TreeNode r = new TreeNode(e, ra, rb);
			buffer.add(r);
		}
		
		this.root = buffer.get(0);
	}
	
	// Build ultrametric tree U
	public Tree(Tree R, MST T, int[] CW) {
		double height = 0;
		
		Edge[] t = T.getEdges();
		for(int i = 0; i<t.length; i++){
			t[i].setWeight(CW[i]);
		}
		
		QuickSort.ordenar(t);
		List<TreeNode> buffer = new ArrayList<TreeNode>();
		
		for(int i = 0; i<t.length; i++){
			Edge e = t[i];
			
			TreeNode ua = findSubTree(buffer, e.getA());
			TreeNode ub = findSubTree(buffer, e.getB());
			TreeNode u = new TreeNode(e, ua, ub);
			
			height = (double) t[i].getWeight()/2;
			u.setLeftHeight(height - (ua != null ? ua.getHeight() : 0));
			u.setRightHeight(height - (ub != null ? ub.getHeight() : 0));
			
			buffer.add(u);
		}
		
		this.root = buffer.get(0);
	}

	private TreeNode findSubTree(List<TreeNode> l, int id){
		for(TreeNode e : l){
			if(e.contains(id)) {
				l.remove(e);
				return e;
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return root.toString();
	}

	// Pre-process R for the lca queries
	public void process(MST t) {
		for(int i = 0; i<t.getEdges().length; i++){
			this.root.process(t.getEdges()[i].getA(), t.getEdges()[i].getB(), i);
		}
	}

	// Find the index of the lowest common ancestor between a and b
	public int lca(int a, int b) {
		return root.lca(a,b);
	}

	public boolean isUltrametric() {
		return root.isUltrametric() > 0;
	}
}

class MST {
	private Edge[] edges;

	public MST(Edge[] edges) {
		this.edges = edges;
	}

	public Edge[] getEdges() {
		return edges;
	}
	
	@Override
	public String toString(){
		String result = "";
		for(Edge e : edges){
			result += Util.toChar(e.getA()) + " --(" + e.getWeight() + ")-- " + Util.toChar(e.getB()) + "\n"; 
		}
		
		return result;
	}
}

class Edge{
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

class Forest {
	private Set<ForestNode> forest;
	
	
	public Forest(){
		this.forest = new HashSet<ForestNode>();
	}
	
	public void MAKE_SET(int i){
		forest.add(new ForestNode(i));
	}
	
	public ForestNode FIND_SET(int i){
		for(ForestNode e : forest){
			if(e.getId() == i) return e.findHeader();
		}
		return null;
	}
	
	public void UNION(int u, int v){
		ForestNode a = null, b = null;
		for(ForestNode e : forest){
			if(e.getId() == u) a = e;
			else if(e.getId() == v) b = e;
		}
		
		// Find best way to join nodes
		if(a.getParent() == a) a.setParent(b);
		else if(b.getParent() == b) b.setParent(a);
		else{
			a = a.reverse(null);
			a.setParent(b);
		}
	}
	
	public Set<ForestNode> getForest(){
		return this.forest;
	}

	public MST toMST(Graph Gh) {
		Edge[] edges = new Edge[forest.size()-1];
		int idx = 0;
		for(ForestNode e : forest){
			if(e.getParent() != e){
				edges[idx] = new Edge(e.getId(), e.getParent().getId(), Gh.ADJENCY_MATRIX[e.getId()][e.getParent().getId()]);
				idx++;
			}
		}
		
		return new MST(edges);
	}
}

class ForestNode{
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

class TreeNode{
	private Edge edge;
	private TreeNode leftChild;
	private TreeNode rightChild;
	private double leftHeight;
	private double rightHeight;
	
	private int CWindex;
	
	private static final int FOUND_A = -5;
	private static final int FOUND_B = -7;
	private static final int NOT_FOUND = -9;
	
	public TreeNode(Edge edge, TreeNode leftChild, TreeNode rightChild){
		this.edge = edge;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
	}

	public TreeNode getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(TreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public TreeNode getRightChild() {
		return rightChild;
	}

	public void setRightChild(TreeNode rightChild) {
		this.rightChild = rightChild;
	}
	
	// DFS routine
	public boolean contains(int id){
		boolean found = false;
		if(this.edge.getA() == id || this.edge.getB() == id) return true;
		if(this.leftChild != null) found = this.leftChild.contains(id);
		if(this.rightChild != null) return found || this.rightChild.contains(id);
		return found;
	}

	public double getLeftHeight() {
		return leftHeight;
	}

	public void setLeftHeight(double leftHeight) {
		this.leftHeight = leftHeight;
	}

	public double getRightHeight() {
		return rightHeight;
	}
	
	// Get node overall height (since both sides are equal we choose left just because)
	public double getHeight(){
		if(leftChild != null) return this.leftHeight + leftChild.getHeight();
		else return this.leftHeight;
	}

	public void setRightHeight(double rightHeight) {
		this.rightHeight = rightHeight;
	}

	public int getCWindex() {
		return CWindex;
	}

	public void setCWindex(int cWindex) {
		CWindex = cWindex;
	}

	public void process(int a, int b, int i) {
		if(edge.getA() == a && edge.getB() == b) this.CWindex = i;
		else{
			if(leftChild != null) leftChild.process(a, b, i);
			if(rightChild != null) rightChild.process(a, b, i);
		}
	}

	public int lca(int a, int b) {
		int left = NOT_FOUND, right = NOT_FOUND;
		
		// Locate left data
		if(leftChild != null){
			left = leftChild.lca(a, b);
		}else{
			left = edge.getA() == a ? FOUND_A : edge.getA() == b ? FOUND_B : NOT_FOUND;
		}
		
		// Locate right data
		if(rightChild != null){
			right = rightChild.lca(a, b);
		}else{
			right = edge.getB() == a ? FOUND_A : edge.getB() == b ? FOUND_B : NOT_FOUND;
		}
		
		// Reasoning
		if(left >= 0) return left;
		else if(right >= 0) return right;
		else if(left != NOT_FOUND && right != NOT_FOUND && left != right) return CWindex;
		else if(left != NOT_FOUND) return left;
		else if(right != NOT_FOUND) return right;
		else return NOT_FOUND;
	}
	
	@Override
	public String toString(){
		String str = "(" + Util.toChar(edge.getA()) + ", " + Util.toChar(edge.getB()) + ") --> ";
		
		if(leftChild != null) str += "(" + Util.toChar(leftChild.getEdge().getA()) + ", " + Util.toChar(leftChild.getEdge().getB()) + ")";
		else str += Util.toChar(edge.getA());
		
		if(leftHeight > 0) str += "{" + leftHeight + "} ";
		else str += " ";
		
		if(rightChild != null) str += "(" + Util.toChar(rightChild.getEdge().getA()) + ", " + Util.toChar(rightChild.getEdge().getB()) + ")";
		else str += Util.toChar(edge.getB());
		
		if(rightHeight > 0) str += "{" + rightHeight + "}\n";
		else str += "\n";
		
		if(leftChild != null) str += leftChild.toString();
		if(rightChild != null) str += rightChild.toString();
		
		return str;
	}

	public double isUltrametric() {
		double left;
		double right;
		
		if(leftChild == null) left = leftHeight;
		else left = leftHeight + leftChild.isUltrametric();
		
		if(rightChild == null) right = rightHeight;
		else right = rightHeight + rightChild.isUltrametric();
		
		if(leftHeight > 0 && rightHeight > 0 && left == right) return left;
		else return -1;
	}
}

class QuickSort
{
	public static void ordenar(Edge[] vetor)
	{
		ordenar(vetor, 0, vetor.length - 1);
	}

	private static void ordenar(Edge[] vetor, int inicio, int fim)
	{
		if (inicio < fim)
		{
			int posicaoPivo = separar(vetor, inicio, fim);
			ordenar(vetor, inicio, posicaoPivo - 1);
			ordenar(vetor, posicaoPivo + 1, fim);
		}
	}

	private static int separar(Edge[] vetor, int inicio, int fim)
	{
		Edge pivo = vetor[inicio];
		int i = inicio + 1, f = fim;
		while (i <= f)
		{
			if (vetor[i].getWeight() <= pivo.getWeight())
				i++;
			else if (pivo.getWeight() < vetor[f].getWeight())
				f--;
			else
			{
				Edge troca = vetor[i];
				vetor[i] = vetor[f];
				vetor[f] = troca;
				i++;
				f--;
			}
		}
		vetor[inicio] = vetor[f];
		vetor[f] = pivo;
		return f;
	}
}

class Util {

	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // NOTE: Usually this should be a field rather than a method
	    // variable so that it is not re-seeded every call.
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	/**
	 * Returns terminal number of n recursively.
	 * <code>terminal(n) = n + (n-1) + (n-2) + ... + 1</code>
	 * 
	 * @param n value
	 * @return Terminal integer
	 */
	public static int terminal(int n){
		if(n <= 1) return 1;
		else return n + terminal(n-1);
	}

	/**
	 * Returns the equivalent char to an integer i.
	 * If the character is out of [A-Z] range, then return '?'.
	 * 
	 * @param i integer
	 * @return char in [A-Z]
	 */
	public static char toChar(int i){
		int converted = i+65;
		return converted <= 90 && converted >= 65 ? (char) (i + 65) : '?';
	}
}
