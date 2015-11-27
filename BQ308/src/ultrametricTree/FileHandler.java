package ultrametricTree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {
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
