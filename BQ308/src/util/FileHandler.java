package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sequenceAlign.Costs;
import sequenceAlign.Ordering;
import sequenceAlign.Sequence;

public class FileHandler {
	public final static File INPUT1 = new File("text-files/input-project1.txt");
	public final static File INPUT2 = new File("text-files/input-project2.txt");
	public final static int MATRIX = 0;
	public final static int ORDERING = 1;
	public final static int SEQ1 = 2;
	public final static int SEQ2 = 3;
	
	// All from SusD superfamily protein
	public final static File A6KXE4_FASTA = new File("text-files/project2/A6KXE4.fasta.txt");
	public final static File A6KYC4_FASTA = new File("text-files/project2/A6KYC4.fasta.txt");
	public final static File A6L054_FASTA = new File("text-files/project2/A6L054.fasta.txt");
	public final static File A6L326_FASTA = new File("text-files/project2/A6L326.fasta.txt");
	public final static File A6LIX4_FASTA = new File("text-files/project2/A6LIX4.fasta.txt");
	public final static File Q5LEF0_FASTA = new File("text-files/project2/Q5LEF0.fasta.txt");
	public final static File Q5LHN1_FASTA = new File("text-files/project2/Q5LHN1.fasta.txt");
	public final static File Q8A577_FASTA = new File("text-files/project2/Q8A577.fasta.txt");
	
	// All from ABC_trans family protein
	public final static File O95477_FASTA = new File("text-files/project2/O95477.fasta.txt");
	public final static File P41233_FASTA = new File("text-files/project2/P41233.fasta.txt");
	public final static File O35379_FASTA = new File("text-files/project2/O35379.fasta.txt");
	public final static File P33527_FASTA = new File("text-files/project2/P33527.fasta.txt");
	public final static File Q6UR05_FASTA = new File("text-files/project2/Q6UR05.fasta.txt");
	public final static File Q5F364_FASTA = new File("text-files/project2/Q5F364.fasta.txt");
	public final static File Q9H222_FASTA = new File("text-files/project2/Q9H222.fasta.txt");
	public final static File Q99PE8_FASTA = new File("text-files/project2/Q99PE8.fasta.txt");
	
	
	public final static File OUTPUT_A = new File("text-files/project2/matrixes/A.txt");
	public final static File OUTPUT_B = new File("text-files/project2/matrixes/B.txt");
	public final static File OUTPUT_C = new File("text-files/project2/matrixes/C.txt");
	
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
	
	public static List<String> read(){
		try {
			FileReader fr = new FileReader(INPUT1);
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
