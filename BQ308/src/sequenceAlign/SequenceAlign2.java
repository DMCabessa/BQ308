package sequenceAlign;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

import util.FileHandler;
import util.Matrices;

public class SequenceAlign2 {
	private static int[][] COST_MATRIX;
	
	public static void main(String[] args) throws FileNotFoundException {
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
