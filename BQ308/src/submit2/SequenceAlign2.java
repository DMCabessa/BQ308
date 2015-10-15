package submit2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SequenceAlign2 {
	private static int[][] COST_MATRIX;
	
	public static void main(String[] args) throws IOException {
		Map<Integer, Object> data = FileHandler.read(FileHandler.INPUT2);
		COST_MATRIX = (int[][]) data.get(FileHandler.MATRIX);
		Ordering order = (Ordering) data.get(FileHandler.ORDERING);
		Sequence s1  = (Sequence) data.get(FileHandler.SEQ1);
		Sequence s2  = (Sequence) data.get(FileHandler.SEQ2);
		String seq1 = "-"+s1.getTrace();
		String seq2 = "-"+s2.getTrace();
		
		// Initialize matrixes
		double[][] A = init(seq2.length(),seq1.length(),Double.NEGATIVE_INFINITY);
		double[][] B = init(seq2.length(),seq1.length(),Double.NEGATIVE_INFINITY);
		double[][] C = init(seq2.length(),seq1.length(),Double.NEGATIVE_INFINITY);
		
		Origin[][] oA = new Origin[seq2.length()][seq1.length()];
		Origin[][] oB = new Origin[seq2.length()][seq1.length()];
		Origin[][] oC = new Origin[seq2.length()][seq1.length()];
		
		// First step: fill first line and column
		A[0][0] = 0;
		oA[0][0] = new Origin('S',0,0);
		for(int i = 1; i < seq1.length(); i++){
			B[0][i] = w(i);
			char m = i == 1 ? 'A' : 'B';
			oB[0][i] = new Origin(m,0,i-1);
		}
		for(int i = 1; i < seq2.length(); i++){
			C[i][0] = w(i);
			char m = i == 1 ? 'A' : 'C';
			oC[i][0] = new Origin(m,i-1,0);
		}
		
		// Second step: fill the remaining cells
		for(int i = 1; i < seq2.length(); i++){
			for(int j = 1; j < seq1.length(); j++){
				A[i][j] = p(seq2.charAt(i),seq1.charAt(j)) + Math.max(A[i-1][j-1], Math.max(B[i-1][j-1], C[i-1][j-1]));
				oA[i][j] = new Origin(findPriority(A[i-1][j-1], B[i-1][j-1], C[i-1][j-1], order),i-1,j-1);
				for (int k = 1; k <= j; k++){
					double before = B[i][j];
					B[i][j] = Math.max(B[i][j], Math.max(A[i][j-k] + w(k), C[i][j-k] + w(k)));
					double after = B[i][j];
					if(before != after) oB[i][j] = new Origin(findPriority(A[i][j-k] + w(k), before, C[i][j-k] + w(k), order),i,j-k);
				}
				for (int k = 1; k <= i; k++){
					double before = C[i][j];
					C[i][j] = Math.max(C[i][j], Math.max(A[i-k][j] + w(k), B[i-k][j] + w(k)));
					double after = C[i][j];
					if(before != after) oC[i][j] = new Origin(findPriority(A[i-k][j] + w(k), B[i-k][j] + w(k), before, order),i-k,j);
				}
			}
		}
		if(!FileHandler.OUTPUT_A.exists()) FileHandler.OUTPUT_A.createNewFile();
		if(!FileHandler.OUTPUT_B.exists()) FileHandler.OUTPUT_B.createNewFile();
		if(!FileHandler.OUTPUT_C.exists()) FileHandler.OUTPUT_C.createNewFile();
		
		printMatrix('A',A, new PrintStream(FileHandler.OUTPUT_A));
		printMatrix('B',B, new PrintStream(FileHandler.OUTPUT_B));
		printMatrix('C',C, new PrintStream(FileHandler.OUTPUT_C));
//		printMatrix(oA, System.out);
//		System.out.println("--");
//		printMatrix(oB, System.out);
//		System.out.println("--");
//		printMatrix(oC, System.out);
//		System.out.println("--");
		
		// Third step: backtrace the sequence
		Origin current;
		String backtrace = "";
		int _i = seq2.length()-1, _j = seq1.length()-1; 
		char best = findPriority(A[seq2.length()-1][seq1.length()-1],B[seq2.length()-1][seq1.length()-1],C[seq2.length()-1][seq1.length()-1],order);
		
		if(best == 'A') current = oA[seq2.length()-1][seq1.length()-1];
		else if(best == 'B') current = oB[seq2.length()-1][seq1.length()-1];
		else current = oC[seq2.length()-1][seq1.length()-1];
		
		while(current.getM() != 'S'){
			if(Math.abs(_i - (current.getI())) == 0){
				for(int x = 0; x < Math.abs(_j - (current.getJ())); x++){
					backtrace += "R";
				}
			}else if(Math.abs(_j - (current.getJ())) == 0){
				for(int x = 0; x < Math.abs(_i - (current.getI())); x++){
					backtrace += "I";
				}
			}else{
				backtrace += "S";
			}
			
			_i = current.getI();
			_j = current.getJ();
			
			if(current.getM() == 'A') current = oA[current.getI()][current.getJ()];
			else if(current.getM() == 'B') current = oB[current.getI()][current.getJ()];
			else current = oC[current.getI()][current.getJ()];
		}
		
		backtrace = new StringBuilder(backtrace).reverse().toString();
		
		// Fourth step: mount align sequence
		int p1 = 0, p2 = 0;
		String aseq1 = "", aseq2 = "";
		seq1 = seq1.substring(1);
		seq2 = seq2.substring(1);
		
		for(int i = 0; i<backtrace.length(); i++){
			switch(backtrace.charAt(i)){
				case 'I':
					aseq1 += "-";
					aseq2 += seq2.charAt(p2);
					p2++;
					break;
				case 'R':
					aseq1 += seq1.charAt(p1);
					aseq2 += "-";
					p1++;
					break;
				case 'M':
					aseq1 += seq1.charAt(p1);
					aseq2 += seq2.charAt(p2);
					p1++;
					p2++;
					break;
				case 'S':
					aseq1 += seq1.charAt(p1);
					aseq2 += seq2.charAt(p2);
					p1++;
					p2++;
					break;
				default:
					break;
			}
		}
		
		// Step five: write 'matches' sequence
		String matches = "";
		for(int i = 0; i<aseq1.length(); i++){
			if(aseq1.charAt(i) == aseq2.charAt(i)) matches += "|";
			else if(p(aseq1.charAt(i),aseq2.charAt(i)) >= 0) matches += "+";
			else matches += " ";
		}
		
		// Step six: split sequence into blocks
		printBlock(aseq1,matches,aseq2,50, s1.getName(), s2.getName());
	}
	
	private static void printBlock(String aseq1, String matches, String aseq2, int blockSize, String name1, String name2) {
		int p = 0;
		while(p + blockSize < aseq1.length()){
			String leftSign = (p+1)+": ";
			String space = "";
			for(int i = 0; i<leftSign.length(); i++) space += " ";
			
			System.out.print(leftSign);
			System.out.print(aseq1.substring(p, p+blockSize));
			System.out.println(" "+name1);
			
			System.out.print(space);
			System.out.println(matches.substring(p, p+blockSize));
			
			System.out.print(leftSign);
			System.out.print(aseq2.substring(p, p+blockSize));
			System.out.println(" "+name2);
			
			System.out.println();
			
			p += blockSize;
		}
		String leftSign = (p+1)+": ";
		String space = "";
		for(int i = 0; i<leftSign.length(); i++) space += " ";
		
		System.out.print(leftSign);
		System.out.print(aseq1.substring(p, aseq1.length()));
		System.out.println(" "+name1);
		
		System.out.print(space);
		System.out.println(matches.substring(p, matches.length()));
		
		System.out.print(leftSign);
		System.out.print(aseq2.substring(p, aseq2.length()));
		System.out.println(" "+name2);
	}

	// Find the matrix of origin given a step
	private static char findPriority(double a, double b, double c, Ordering order) {
		if(a > b){
			if(a > c){
				return 'A';
			}else if(a == c){
				return order.isPriorityOver('S', 'I') ? 'A' : 'C';
			}else{
				return 'C';
			}
		}else if (a == b){
			if(a > c){
				return order.isPriorityOver('S', 'R') ? 'A' : 'B';
			}else if(a == c){
				if(order.isPriorityOver('S', 'R')){
					return order.isPriorityOver('S', 'I') ? 'A' : 'C'; 
				}else{
					return order.isPriorityOver('R', 'I') ? 'B' : 'C';
				}
			}else{
				return 'C';
			}
		}else{
			if(b > c){
				return 'B';
			}else if(b == c){
				return order.isPriorityOver('R', 'I') ? 'B' : 'C';
			}else{
				return 'C';
			}
		}
	}

	// Calculate the cost of a block of size i
	public static int w(int i){
		int pos = Matrices.HEADERS.length - 1;
		int open = 2 * COST_MATRIX[0][pos];
		int stretch = COST_MATRIX[0][pos] * (i-1);
		
		return open + stretch;
	}
	
	// Find the value of a certain match
	public static int p(char a, char b){
		for(int i = 0; i < Matrices.HEADERS.length; i++){
			for(int j = 0; j < Matrices.HEADERS.length; j++){
				if(Matrices.HEADERS[i] == a && Matrices.HEADERS[j] == b){
					return COST_MATRIX[i][j];
				}
			}
		}
		
		int pos = Matrices.HEADERS.length - 1;
		int open = 2 * COST_MATRIX[0][pos];
		return open;
	}

	// Initialize an integer matrix with a specific value
	public static double[][] init(int i, int j, double val){
		double[][] matrix = new double[i][j];
		for(double[] column : matrix){
			Arrays.fill(column, val);
		}
		
		return matrix;
	}
	
	// Print a matrix into a certain stream
	public static void printMatrix(char m, double[][] M, PrintStream ps){
		for (int i = 0; i < M.length; i++) {
		    for (int j = 0; j < M[0].length; j++) {
		        ps.println(m+"["+i+"]["+j+"] = "+M[i][j]+"\n");
		    }
		}
	}
	
	// Print a matrix into a certain stream
	public static void printMatrix(Origin[][] M, PrintStream ps){
		for (int i = 0; i < M.length; i++) {
		    for (int j = 0; j < M[0].length; j++) {
		        ps.print(M[i][j] + "\t");
		    }
		    ps.print("\n");
		}
	}
}

class FileHandler {
	public final static File INPUT2 = new File("input-project2.txt");
	public final static int MATRIX = 0;
	public final static int ORDERING = 1;
	public final static int SEQ1 = 2;
	public final static int SEQ2 = 3;
	
	// All from SusD superfamily protein
	public final static File A6KXE4_FASTA = new File("A6KXE4.fasta.txt");
	public final static File A6KYC4_FASTA = new File("A6KYC4.fasta.txt");
	public final static File A6L054_FASTA = new File("A6L054.fasta.txt");
	public final static File A6L326_FASTA = new File("A6L326.fasta.txt");
	public final static File A6LIX4_FASTA = new File("A6LIX4.fasta.txt");
	public final static File Q5LEF0_FASTA = new File("Q5LEF0.fasta.txt");
	public final static File Q5LHN1_FASTA = new File("Q5LHN1.fasta.txt");
	public final static File Q8A577_FASTA = new File("Q8A577.fasta.txt");
	
	// All from ABC_trans family protein
	public final static File O95477_FASTA = new File("O95477.fasta.txt");
	public final static File P41233_FASTA = new File("P41233.fasta.txt");
	public final static File O35379_FASTA = new File("O35379.fasta.txt");
	public final static File P33527_FASTA = new File("P33527.fasta.txt");
	public final static File Q6UR05_FASTA = new File("Q6UR05.fasta.txt");
	public final static File Q5F364_FASTA = new File("Q5F364.fasta.txt");
	public final static File Q9H222_FASTA = new File("Q9H222.fasta.txt");
	public final static File Q99PE8_FASTA = new File("Q99PE8.fasta.txt");
	
	
	public final static File OUTPUT_A = new File("A.txt");
	public final static File OUTPUT_B = new File("B.txt");
	public final static File OUTPUT_C = new File("C.txt");
	
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
	
	private static String readSequence(File file){
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String sequence = "", line;
			
			// Skip first line
			br.readLine();
			
			while ((line = br.readLine()) != null) {
		        sequence += line;
		    }
			
			br.close();
			
			return sequence;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getOrdering(File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			br.readLine();
			String ordering = br.readLine();
			
			br.close();
			return ordering;
		} catch (IOException e) {}
		return null;
	}

	public static Map<Integer, Object> read(File f) {
		try {
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			HashMap<Integer, Object> data = new HashMap<Integer, Object>();
			
			String matrix = br.readLine();
			switch(matrix){
				case "BLOSUM30":
					data.put(MATRIX, Matrices.BLOSUM30);
					break;
				case "BLOSUM62":
					data.put(MATRIX, Matrices.BLOSUM62);
					break;
				case "BLOSUM90":
					data.put(MATRIX, Matrices.BLOSUM90);
					break;
				case "PAM10":
					data.put(MATRIX, Matrices.PAM10);
					break;
				case "PAM120":
					data.put(MATRIX, Matrices.PAM120);
					break;
				case "PAM250":
					data.put(MATRIX, Matrices.PAM250);
					break;
			}
			
			String ordering = br.readLine();
			data.put(ORDERING, new Ordering(ordering));
			
			String seq1 = br.readLine();
			data.put(SEQ1, getSequence(seq1));
			
			String seq2 = br.readLine();
			data.put(SEQ2, getSequence(seq2));
			
			br.close();
			return data;
		} catch (IOException e) {}
		return null;
	}

	private static Sequence getSequence(String seq) {
		switch(seq){
			case "A6KXE4":
				return new Sequence(seq, readSequence(A6KXE4_FASTA));
			case "A6KYC4":
				return new Sequence(seq, readSequence(A6KYC4_FASTA));
			case "A6L054":
				return new Sequence(seq, readSequence(A6L054_FASTA));
			case "A6L326":
				return new Sequence(seq, readSequence(A6L326_FASTA));
			case "A6LIX4":
				return new Sequence(seq, readSequence(A6LIX4_FASTA));
			case "Q5LEF0":
				return new Sequence(seq, readSequence(Q5LEF0_FASTA));
			case "Q5LHN1":
				return new Sequence(seq, readSequence(Q5LHN1_FASTA));
			case "Q8A577":
				return new Sequence(seq, readSequence(Q8A577_FASTA));
			case "O95477":
				return new Sequence(seq, readSequence(O95477_FASTA));
			case "P41233":
				return new Sequence(seq, readSequence(P41233_FASTA));
			case "O35379":
				return new Sequence(seq, readSequence(O35379_FASTA));
			case "P33527":
				return new Sequence(seq, readSequence(P33527_FASTA));
			case "Q6UR05":
				return new Sequence(seq, readSequence(Q6UR05_FASTA));
			case "Q5F364":
				return new Sequence(seq, readSequence(Q5F364_FASTA));
			case "Q9H222":
				return new Sequence(seq, readSequence(Q9H222_FASTA));
			case "Q99PE8":
				return new Sequence(seq, readSequence(Q99PE8_FASTA));
			default:
				return null;
		}
	}

}

class Origin {
	
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

class Ordering {

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

class Sequence{
	
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

interface Matrices {

	char[] HEADERS = {'A','R','N','D','C','Q','E','G','H','I','L','K','M','F','P','S','T','W','Y','V','B','Z','X','*'};

	int[][] PAM10 = {{7,-10,-7,-6,-10,-7,-5,-4,-11,-8,-9,-10,-8,-12,-4,-3,-3,-20,-11,-5,-6,-6,-6,-23},{-10,9,-9,-17,-11,-4,-15,-13,-4,-8,-12,-2,-7,-12,-7,-6,-10,-5,-14,-11,-11,-7,-9,-23},{-7,-9,9,-1,-17,-7,-5,-6,-2,-8,-10,-4,-15,-12,-9,-2,-5,-11,-7,-12,7,-6,-6,-23},{-6,-17,-1,8,-21,-6,0,-6,-7,-11,-19,-8,-17,-21,-12,-7,-8,-21,-17,-11,7,-1,-9,-23},{-10,-11,-17,-21,10,-20,-20,-13,-10,-9,-21,-20,-20,-19,-11,-6,-11,-22,-7,-9,-18,-20,-13,-23},{-7,-4,-7,-6,-20,9,-1,-10,-2,-11,-8,-6,-7,-19,-6,-8,-9,-19,-18,-10,-6,7,-8,-23},{-5,-15,-5,0,-20,-1,8,-7,-9,-8,-13,-7,-10,-20,-9,-7,-9,-23,-11,-10,-1,7,-8,-23},{-4,-13,-6,-6,-13,-10,-7,7,-13,-17,-14,-10,-12,-12,-10,-4,-10,-21,-20,-9,-6,-8,-8,-23},{-11,-4,-2,-7,-10,-2,-9,-13,10,-13,-9,-10,-17,-9,-7,-9,-11,-10,-6,-9,-4,-4,-8,-23},{-8,-8,-8,-11,-9,-11,-8,-17,-13,9,-4,-9,-3,-5,-12,-10,-5,-20,-9,-1,-9,-9,-8,-23},{-9,-12,-10,-19,-21,-8,-13,-14,-9,-4,7,-11,-2,-5,-10,-12,-10,-9,-10,-5,-12,-10,-9,-23},{-10,-2,-4,-8,-20,-6,-7,-10,-10,-9,-11,7,-4,-20,-10,-7,-6,-18,-12,-13,-5,-6,-8,-23},{-8,-7,-15,-17,-20,-7,-10,-12,-17,-3,-2,-4,12,-7,-11,-8,-7,-19,-17,-4,-16,-8,-9,-23},{-12,-12,-12,-21,-19,-19,-20,-12,-9,-5,-5,-20,-7,9,-13,-9,-12,-7,-1,-12,-14,-20,-12,-23},{-4,-7,-9,-12,-11,-6,-9,-10,-7,-12,-10,-10,-11,-13,8,-4,-7,-20,-20,-9,-10,-7,-8,-23},{-3,-6,-2,-7,-6,-8,-7,-4,-9,-10,-12,-7,-8,-9,-4,7,-2,-8,-10,-10,-4,-8,-6,-23},{-3,-10,-5,-8,-11,-9,-9,-10,-11,-5,-10,-6,-7,-12,-7,-2,8,-19,-9,-6,-6,-9,-7,-23},{-20,-5,-11,-21,-22,-19,-23,-21,-10,-20,-9,-18,-19,-7,-20,-8,-19,13,-8,-22,-13,-21,-16,-23},{-11,-14,-7,-17,-7,-18,-11,-20,-6,-9,-10,-12,-17,-1,-20,-10,-9,-8,10,-10,-9,-13,-11,-23},{-5,-11,-12,-11,-9,-10,-10,-9,-9,-1,-5,-13,-4,-12,-9,-10,-6,-22,-10,8,-11,-10,-8,-23},{-6,-11,7,7,-18,-6,-1,-6,-4,-9,-12,-5,-16,-14,-10,-4,-6,-13,-9,-11,7,-3,-8,-23},{-6,-7,-6,-1,-20,7,7,-8,-4,-9,-10,-6,-8,-20,-7,-8,-9,-21,-13,-10,-3,7,-8,-23},{-6,-9,-6,-9,-13,-8,-8,-8,-8,-8,-9,-8,-9,-12,-8,-6,-7,-16,-11,-8,-8,-8,-8,-23},{-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,-23,1}};

	int[][] PAM120 = {{3,-3,-1,0,-3,-1,0,1,-3,-1,-3,-2,-2,-4,1,1,1,-7,-4,0,0,-1,-1,-8},{-3,6,-1,-3,-4,1,-3,-4,1,-2,-4,2,-1,-5,-1,-1,-2,1,-5,-3,-2,-1,-2,-8},{-1,-1,4,2,-5,0,1,0,2,-2,-4,1,-3,-4,-2,1,0,-4,-2,-3,3,0,-1,-8},{0,-3,2,5,-7,1,3,0,0,-3,-5,-1,-4,-7,-3,0,-1,-8,-5,-3,4,3,-2,-8},{-3,-4,-5,-7,9,-7,-7,-4,-4,-3,-7,-7,-6,-6,-4,0,-3,-8,-1,-3,-6,-7,-4,-8},{-1,1,0,1,-7,6,2,-3,3,-3,-2,0,-1,-6,0,-2,-2,-6,-5,-3,0,4,-1,-8},{0,-3,1,3,-7,2,5,-1,-1,-3,-4,-1,-3,-7,-2,-1,-2,-8,-5,-3,3,4,-1,-8},{1,-4,0,0,-4,-3,-1,5,-4,-4,-5,-3,-4,-5,-2,1,-1,-8,-6,-2,0,-2,-2,-8},{-3,1,2,0,-4,3,-1,-4,7,-4,-3,-2,-4,-3,-1,-2,-3,-3,-1,-3,1,1,-2,-8},{-1,-2,-2,-3,-3,-3,-3,-4,-4,6,1,-3,1,0,-3,-2,0,-6,-2,3,-3,-3,-1,-8},{-3,-4,-4,-5,-7,-2,-4,-5,-3,1,5,-4,3,0,-3,-4,-3,-3,-2,1,-4,-3,-2,-8},{-2,2,1,-1,-7,0,-1,-3,-2,-3,-4,5,0,-7,-2,-1,-1,-5,-5,-4,0,-1,-2,-8},{-2,-1,-3,-4,-6,-1,-3,-4,-4,1,3,0,8,-1,-3,-2,-1,-6,-4,1,-4,-2,-2,-8},{-4,-5,-4,-7,-6,-6,-7,-5,-3,0,0,-7,-1,8,-5,-3,-4,-1,4,-3,-5,-6,-3,-8},{1,-1,-2,-3,-4,0,-2,-2,-1,-3,-3,-2,-3,-5,6,1,-1,-7,-6,-2,-2,-1,-2,-8},{1,-1,1,0,0,-2,-1,1,-2,-2,-4,-1,-2,-3,1,3,2,-2,-3,-2,0,-1,-1,-8},{1,-2,0,-1,-3,-2,-2,-1,-3,0,-3,-1,-1,-4,-1,2,4,-6,-3,0,0,-2,-1,-8},{-7,1,-4,-8,-8,-6,-8,-8,-3,-6,-3,-5,-6,-1,-7,-2,-6,12,-2,-8,-6,-7,-5,-8},{-4,-5,-2,-5,-1,-5,-5,-6,-1,-2,-2,-5,-4,4,-6,-3,-3,-2,8,-3,-3,-5,-3,-8},{0,-3,-3,-3,-3,-3,-3,-2,-3,3,1,-4,1,-3,-2,-2,0,-8,-3,5,-3,-3,-1,-8},{0,-2,3,4,-6,0,3,0,1,-3,-4,0,-4,-5,-2,0,0,-6,-3,-3,4,2,-1,-8},{-1,-1,0,3,-7,4,4,-2,1,-3,-3,-1,-2,-6,-1,-1,-2,-7,-5,-3,2,4,-1,-8},{-1,-2,-1,-2,-4,-1,-1,-2,-2,-1,-2,-2,-2,-3,-2,-1,-1,-5,-3,-1,-1,-1,-2,-8},{-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,1}};
	
	int[][] PAM250 = {{2,-2,0,0,-2,0,0,1,-1,-1,-2,-1,-1,-3,1,1,1,-6,-3,0,0,0,0,-8},{-2,6,0,-1,-4,1,-1,-3,2,-2,-3,3,0,-4,0,0,-1,2,-4,-2,-1,0,-1,-8},{0,0,2,2,-4,1,1,0,2,-2,-3,1,-2,-3,0,1,0,-4,-2,-2,2,1,0,-8},{0,-1,2,4,-5,2,3,1,1,-2,-4,0,-3,-6,-1,0,0,-7,-4,-2,3,3,-1,-8},{-2,-4,-4,-5,12,-5,-5,-3,-3,-2,-6,-5,-5,-4,-3,0,-2,-8,0,-2,-4,-5,-3,-8},{0,1,1,2,-5,4,2,-1,3,-2,-2,1,-1,-5,0,-1,-1,-5,-4,-2,1,3,-1,-8},{0,-1,1,3,-5,2,4,0,1,-2,-3,0,-2,-5,-1,0,0,-7,-4,-2,3,3,-1,-8},{1,-3,0,1,-3,-1,0,5,-2,-3,-4,-2,-3,-5,0,1,0,-7,-5,-1,0,0,-1,-8},{-1,2,2,1,-3,3,1,-2,6,-2,-2,0,-2,-2,0,-1,-1,-3,0,-2,1,2,-1,-8},{-1,-2,-2,-2,-2,-2,-2,-3,-2,5,2,-2,2,1,-2,-1,0,-5,-1,4,-2,-2,-1,-8},{-2,-3,-3,-4,-6,-2,-3,-4,-2,2,6,-3,4,2,-3,-3,-2,-2,-1,2,-3,-3,-1,-8},{-1,3,1,0,-5,1,0,-2,0,-2,-3,5,0,-5,-1,0,0,-3,-4,-2,1,0,-1,-8},{-1,0,-2,-3,-5,-1,-2,-3,-2,2,4,0,6,0,-2,-2,-1,-4,-2,2,-2,-2,-1,-8},{-3,-4,-3,-6,-4,-5,-5,-5,-2,1,2,-5,0,9,-5,-3,-3,0,7,-1,-4,-5,-2,-8},{1,0,0,-1,-3,0,-1,0,0,-2,-3,-1,-2,-5,6,1,0,-6,-5,-1,-1,0,-1,-8},{1,0,1,0,0,-1,0,1,-1,-1,-3,0,-2,-3,1,2,1,-2,-3,-1,0,0,0,-8},{1,-1,0,0,-2,-1,0,0,-1,0,-2,0,-1,-3,0,1,3,-5,-3,0,0,-1,0,-8},{-6,2,-4,-7,-8,-5,-7,-7,-3,-5,-2,-3,-4,0,-6,-2,-5,17,0,-6,-5,-6,-4,-8},{-3,-4,-2,-4,0,-4,-4,-5,0,-1,-1,-4,-2,7,-5,-3,-3,0,10,-2,-3,-4,-2,-8},{0,-2,-2,-2,-2,-2,-2,-1,-2,4,2,-2,2,-1,-1,-1,0,-6,-2,4,-2,-2,-1,-8},{0,-1,2,3,-4,1,3,0,1,-2,-3,1,-2,-4,-1,0,0,-5,-3,-2,3,2,-1,-8},{0,0,1,3,-5,3,3,0,2,-2,-3,0,-2,-5,0,0,-1,-6,-4,-2,2,3,-1,-8},{0,-1,0,-1,-3,-1,-1,-1,-1,-1,-1,-1,-1,-2,-1,0,0,-4,-2,-1,-1,-1,-1,-8},{-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,-8,1}};
	
	int[][] BLOSUM30 = {{4,-1,0,0,-3,1,0,0,-2,0,-1,0,1,-2,-1,1,1,-5,-4,1,0,0,0,-7},{-1,8,-2,-1,-2,3,-1,-2,-1,-3,-2,1,0,-1,-1,-1,-3,0,0,-1,-2,0,-1,-7},{0,-2,8,1,-1,-1,-1,0,-1,0,-2,0,0,-1,-3,0,1,-7,-4,-2,4,-1,0,-7},{0,-1,1,9,-3,-1,1,-1,-2,-4,-1,0,-3,-5,-1,0,-1,-4,-1,-2,5,0,-1,-7},{-3,-2,-1,-3,17,-2,1,-4,-5,-2,0,-3,-2,-3,-3,-2,-2,-2,-6,-2,-2,0,-2,-7},{1,3,-1,-1,-2,8,2,-2,0,-2,-2,0,-1,-3,0,-1,0,-1,-1,-3,-1,4,0,-7},{0,-1,-1,1,1,2,6,-2,0,-3,-1,2,-1,-4,1,0,-2,-1,-2,-3,0,5,-1,-7},{0,-2,0,-1,-4,-2,-2,8,-3,-1,-2,-1,-2,-3,-1,0,-2,1,-3,-3,0,-2,-1,-7},{-2,-1,-1,-2,-5,0,0,-3,14,-2,-1,-2,2,-3,1,-1,-2,-5,0,-3,-2,0,-1,-7},{0,-3,0,-4,-2,-2,-3,-1,-2,6,2,-2,1,0,-3,-1,0,-3,-1,4,-2,-3,0,-7},{-1,-2,-2,-1,0,-2,-1,-2,-1,2,4,-2,2,2,-3,-2,0,-2,3,1,-1,-1,0,-7},{0,1,0,0,-3,0,2,-1,-2,-2,-2,4,2,-1,1,0,-1,-2,-1,-2,0,1,0,-7},{1,0,0,-3,-2,-1,-1,-2,2,1,2,2,6,-2,-4,-2,0,-3,-1,0,-2,-1,0,-7},{-2,-1,-1,-5,-3,-3,-4,-3,-3,0,2,-1,-2,10,-4,-1,-2,1,3,1,-3,-4,-1,-7},{-1,-1,-3,-1,-3,0,1,-1,1,-3,-3,1,-4,-4,11,-1,0,-3,-2,-4,-2,0,-1,-7},{1,-1,0,0,-2,-1,0,0,-1,-1,-2,0,-2,-1,-1,4,2,-3,-2,-1,0,-1,0,-7},{1,-3,1,-1,-2,0,-2,-2,-2,0,0,-1,0,-2,0,2,5,-5,-1,1,0,-1,0,-7},{-5,0,-7,-4,-2,-1,-1,1,-5,-3,-2,-2,-3,1,-3,-3,-5,20,5,-3,-5,-1,-2,-7},{-4,0,-4,-1,-6,-1,-2,-3,0,-1,3,-1,-1,3,-2,-2,-1,5,9,1,-3,-2,-1,-7},{1,-1,-2,-2,-2,-3,-3,-3,-3,4,1,-2,0,1,-4,-1,1,-3,1,5,-2,-3,0,-7},{0,-2,4,5,-2,-1,0,0,-2,-2,-1,0,-2,-3,-2,0,0,-5,-3,-2,5,0,-1,-7},{0,0,-1,0,0,4,5,-2,0,-3,-1,1,-1,-4,0,-1,-1,-1,-2,-3,0,4,0,-7},{0,-1,0,-1,-2,0,-1,-1,-1,0,0,0,0,-1,-1,0,0,-2,-1,0,-1,0,-1,-7},{-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,-7,1}};
	
	int[][] BLOSUM62 = {{4,-1,-2,-2,0,-1,-1,0,-2,-1,-1,-1,-1,-2,-1,1,0,-3,-2,0,-2,-1,0,-4},{-1,5,0,-2,-3,1,0,-2,0,-3,-2,2,-1,-3,-2,-1,-1,-3,-2,-3,-1,0,-1,-4},{-2,0,6,1,-3,0,0,0,1,-3,-3,0,-2,-3,-2,1,0,-4,-2,-3,3,0,-1,-4},{-2,-2,1,6,-3,0,2,-1,-1,-3,-4,-1,-3,-3,-1,0,-1,-4,-3,-3,4,1,-1,-4},{0,-3,-3,-3,9,-3,-4,-3,-3,-1,-1,-3,-1,-2,-3,-1,-1,-2,-2,-1,-3,-3,-2,-4},{-1,1,0,0,-3,5,2,-2,0,-3,-2,1,0,-3,-1,0,-1,-2,-1,-2,0,3,-1,-4},{-1,0,0,2,-4,2,5,-2,0,-3,-3,1,-2,-3,-1,0,-1,-3,-2,-2,1,4,-1,-4},{0,-2,0,-1,-3,-2,-2,6,-2,-4,-4,-2,-3,-3,-2,0,-2,-2,-3,-3,-1,-2,-1,-4},{-2,0,1,-1,-3,0,0,-2,8,-3,-3,-1,-2,-1,-2,-1,-2,-2,2,-3,0,0,-1,-4},{-1,-3,-3,-3,-1,-3,-3,-4,-3,4,2,-3,1,0,-3,-2,-1,-3,-1,3,-3,-3,-1,-4},{-1,-2,-3,-4,-1,-2,-3,-4,-3,2,4,-2,2,0,-3,-2,-1,-2,-1,1,-4,-3,-1,-4},{-1,2,0,-1,-3,1,1,-2,-1,-3,-2,5,-1,-3,-1,0,-1,-3,-2,-2,0,1,-1,-4},{-1,-1,-2,-3,-1,0,-2,-3,-2,1,2,-1,5,0,-2,-1,-1,-1,-1,1,-3,-1,-1,-4},{-2,-3,-3,-3,-2,-3,-3,-3,-1,0,0,-3,0,6,-4,-2,-2,1,3,-1,-3,-3,-1,-4},{-1,-2,-2,-1,-3,-1,-1,-2,-2,-3,-3,-1,-2,-4,7,-1,-1,-4,-3,-2,-2,-1,-2,-4},{1,-1,1,0,-1,0,0,0,-1,-2,-2,0,-1,-2,-1,4,1,-3,-2,-2,0,0,0,-4},{0,-1,0,-1,-1,-1,-1,-2,-2,-1,-1,-1,-1,-2,-1,1,5,-2,-2,0,-1,-1,0,-4},{-3,-3,-4,-4,-2,-2,-3,-2,-2,-3,-2,-3,-1,1,-4,-3,-2,11,2,-3,-4,-3,-2,-4},{-2,-2,-2,-3,-2,-1,-2,-3,2,-1,-1,-2,-1,3,-3,-2,-2,2,7,-1,-3,-2,-1,-4},{0,-3,-3,-3,-1,-2,-2,-3,-3,3,1,-2,1,-1,-2,-2,0,-3,-1,4,-3,-2,-1,-4},{-2,-1,3,4,-3,0,1,-1,0,-3,-4,0,-3,-3,-2,0,-1,-4,-3,-3,4,1,-1,-4},{-1,0,0,1,-3,3,4,-2,0,-3,-3,1,-1,-3,-1,0,-1,-3,-2,-2,1,4,-1,-4},{0,-1,-1,-1,-2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-2,0,0,-2,-1,-1,-1,-1,-1,-4},{-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,-4,1}};
	
	int[][] BLOSUM90 = {{5,-2,-2,-3,-1,-1,-1,0,-2,-2,-2,-1,-2,-3,-1,1,0,-4,-3,-1,-2,-1,-1,-6},{-2,6,-1,-3,-5,1,-1,-3,0,-4,-3,2,-2,-4,-3,-1,-2,-4,-3,-3,-2,0,-2,-6},{-2,-1,7,1,-4,0,-1,-1,0,-4,-4,0,-3,-4,-3,0,0,-5,-3,-4,4,-1,-2,-6},{-3,-3,1,7,-5,-1,1,-2,-2,-5,-5,-1,-4,-5,-3,-1,-2,-6,-4,-5,4,0,-2,-6},{-1,-5,-4,-5,9,-4,-6,-4,-5,-2,-2,-4,-2,-3,-4,-2,-2,-4,-4,-2,-4,-5,-3,-6},{-1,1,0,-1,-4,7,2,-3,1,-4,-3,1,0,-4,-2,-1,-1,-3,-3,-3,-1,4,-1,-6},{-1,-1,-1,1,-6,2,6,-3,-1,-4,-4,0,-3,-5,-2,-1,-1,-5,-4,-3,0,4,-2,-6},{0,-3,-1,-2,-4,-3,-3,6,-3,-5,-5,-2,-4,-5,-3,-1,-3,-4,-5,-5,-2,-3,-2,-6},{-2,0,0,-2,-5,1,-1,-3,8,-4,-4,-1,-3,-2,-3,-2,-2,-3,1,-4,-1,0,-2,-6},{-2,-4,-4,-5,-2,-4,-4,-5,-4,5,1,-4,1,-1,-4,-3,-1,-4,-2,3,-5,-4,-2,-6},{-2,-3,-4,-5,-2,-3,-4,-5,-4,1,5,-3,2,0,-4,-3,-2,-3,-2,0,-5,-4,-2,-6},{-1,2,0,-1,-4,1,0,-2,-1,-4,-3,6,-2,-4,-2,-1,-1,-5,-3,-3,-1,1,-1,-6},{-2,-2,-3,-4,-2,0,-3,-4,-3,1,2,-2,7,-1,-3,-2,-1,-2,-2,0,-4,-2,-1,-6},{-3,-4,-4,-5,-3,-4,-5,-5,-2,-1,0,-4,-1,7,-4,-3,-3,0,3,-2,-4,-4,-2,-6},{-1,-3,-3,-3,-4,-2,-2,-3,-3,-4,-4,-2,-3,-4,8,-2,-2,-5,-4,-3,-3,-2,-2,-6},{1,-1,0,-1,-2,-1,-1,-1,-2,-3,-3,-1,-2,-3,-2,5,1,-4,-3,-2,0,-1,-1,-6},{0,-2,0,-2,-2,-1,-1,-3,-2,-1,-2,-1,-1,-3,-2,1,6,-4,-2,-1,-1,-1,-1,-6},{-4,-4,-5,-6,-4,-3,-5,-4,-3,-4,-3,-5,-2,0,-5,-4,-4,11,2,-3,-6,-4,-3,-6},{-3,-3,-3,-4,-4,-3,-4,-5,1,-2,-2,-3,-2,3,-4,-3,-2,2,8,-3,-4,-3,-2,-6},{-1,-3,-4,-5,-2,-3,-3,-5,-4,3,0,-3,0,-2,-3,-2,-1,-3,-3,5,-4,-3,-2,-6},{-2,-2,4,4,-4,-1,0,-2,-1,-5,-5,-1,-4,-4,-3,0,-1,-6,-4,-4,4,0,-2,-6},{-1,0,-1,0,-5,4,4,-3,0,-4,-4,1,-2,-4,-2,-1,-1,-4,-3,-3,0,4,-1,-6},{-1,-2,-2,-2,-3,-1,-2,-2,-2,-2,-2,-1,-1,-2,-2,-1,-1,-3,-2,-2,-2,-1,-2,-6},{-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,-6,1}};
}
