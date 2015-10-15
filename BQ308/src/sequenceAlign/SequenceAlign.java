package sequenceAlign;

import java.util.List;

import util.FileHandler;

public class SequenceAlign {
	
	public static void main(String[] args) {
		List<String> pairs = FileHandler.read();
		Costs costs = FileHandler.getCosts(FileHandler.INPUT1);
		if(costs != null){
			costs.setOrdering(FileHandler.getOrdering(FileHandler.INPUT1));
		}else{
			costs = new Costs();
		}
		
		// For each pair
		for(String pair : pairs){
			String seq1  = pair.split(" ")[0];
			String seq2  = pair.split(" ")[1];
			
			// Initialize matrixes (costs and operations)
			int[][] M = new int[seq2.length()][seq1.length()];
			char[][] ops = new char[seq2.length()][seq1.length()];
			
			M[0][0] = 0;
			ops[0][0] = 'B';
			
			// First step: fill first line and column
			for(int i = 1; i<seq2.length(); i++){ 
				M[i][0] = costs.getInsert()*i;
				ops[i][0] = 'I';
			}
			for(int i = 1; i<seq1.length(); i++){
				M[0][i] = costs.getRemove()*i;
				ops[0][i] = 'R';
			}
			
			// Second step: fill the remaining cells
			for(int i = 1; i<seq2.length(); i++){
				for (int j = 1; j < seq1.length(); j++) {
					int up = M[i-1][j] + costs.getInsert();
					int left = M[i][j-1] + costs.getRemove();
					int upleft = M[i-1][j-1] + (seq2.charAt(i) == seq1.charAt(j) ? costs.getMatch() : costs.getSubstitution());
							
					int value = Math.max(up, Math.max(left, upleft));
					
					if(up == value) ops[i][j] = 'I';
					if(left == value && costs.getOrdering('R',ops[i][j])) ops[i][j] = 'R';
					if(upleft == value && seq2.charAt(i) != seq1.charAt(j) && costs.getOrdering('S',ops[i][j])) ops[i][j] = 'S';
					if(upleft == value && seq2.charAt(i) == seq1.charAt(j) && costs.getOrdering('M',ops[i][j])) ops[i][j] = 'M';
					
					
					M[i][j] = value;
				}
			}
			
			// Third step: backtrace the sequence
			String bt = backtrace(ops, seq2.length()-1, seq1.length()-1, "");
			
			// Fourth step: mount align sequence
			int p1 = 0, p2 = 0;
			String aseq1 = "", aseq2 = "";
			seq1 = seq1.substring(1);
			seq2 = seq2.substring(1);
			
			for(int i = 0; i<bt.length(); i++){
				switch(bt.charAt(i)){
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
			
//			for (int i = 0; i < seq2.length(); i++) {
//			    for (int j = 0; j < seq1.length(); j++) {
//			        System.out.print(M[i][j] + "("+ops[i][j]+")\t");
//			    }
//			    System.out.print("\n");
//			}
			
			System.out.println(aseq1);
			System.out.println(aseq2);
			System.out.println("######################################################");
		}
	}
	
	public static String backtrace(char[][] ops, int i, int j, String acc){
		switch(ops[i][j]){
			case 'B':
				return new StringBuilder(acc).reverse().toString();
			case 'I':
				return backtrace(ops, (i-1), j, (acc+"I"));
			case 'R':
				return backtrace(ops, i, (j-1), (acc+"R"));
			case 'M':
				return backtrace(ops, (i-1), (j-1), (acc+"M"));
			case 'S':
				return backtrace(ops, (i-1), (j-1), (acc+"S"));
			default:
				return null;
		}
	}
}
