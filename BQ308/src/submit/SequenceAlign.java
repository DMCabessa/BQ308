package submit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SequenceAlign {
	
	public static void main(String[] args) {
		List<String> pairs = FileHandler.read(FileHandler.SEQUENCES);
		Costs costs = FileHandler.getCosts(FileHandler.SEQUENCES);
		if(costs != null){
			costs.setOrdering(FileHandler.getOrdering(FileHandler.SEQUENCES));
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

class FileHandler {
	public final static File SEQUENCES = new File("sequences.txt");
	
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
	
	public static List<String> read(File file){
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			ArrayList<String> list = new ArrayList<String>();
			
			br.readLine();
			br.readLine();
			int lines = Integer.parseInt(br.readLine());
			for(int i = 0; i < lines; i++){
				list.add(br.readLine());
			}
			
			br.close();
			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Costs getCosts(File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			
			String costs = br.readLine();
			int m  = Integer.parseInt(costs.split(" ")[0]);
			int s  = Integer.parseInt(costs.split(" ")[1]);
			int i  = Integer.parseInt(costs.split(" ")[2]);
			int r  = Integer.parseInt(costs.split(" ")[3]);
			
			br.close();
			return new Costs(m,s,i,r);
		} catch (Exception e) {}
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

}

class Costs {
	
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
