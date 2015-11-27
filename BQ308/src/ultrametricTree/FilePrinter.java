package ultrametricTree;

import java.io.File;
import java.io.IOException;

import util.Util;

public class FilePrinter {
	private static final int ML_MIN = 1;
	private static final int ML_MAX = 6;
	
	private static final int MH_MIN = 3;
	private static final int MH_MAX = 12;
	
	private static File generate(int size, String pathname) throws IOException{
		File file = new File(pathname);
		int[][] ml = new int[size][size];
		int[][] mh = new int[size][size];
		String line;
				
		FileHandler.write(size+"", file);
		for(int i = 0; i<size; i++){
			for(int j = 0; j<i; j++){
				int a = Util.randInt(ML_MIN, ML_MAX);
				ml[i][j] = a;
				ml[j][i] = a;
			}
		}
		for(int i = 0; i<size; i++){
			for(int j = 0; j<i; j++){
				int a = Util.randInt(MH_MIN, MH_MAX);
				while(a < ml[i][j]) a = Util.randInt(MH_MIN, MH_MAX);
				mh[i][j] = a;
				mh[j][i] = a;
			}
		}
		
		for(int i = 0; i<size; i++){
			line = "";
			for(int j = 0; j<size; j++){
				line += ml[i][j] + " ";
			}
			FileHandler.write(line, file);
		}
		for(int i = 0; i<size; i++){
			line = "";
			for(int j = 0; j<size; j++){
				line += mh[i][j] + " ";
			}
			FileHandler.write(line, file);
		}
		
		return file;
	}
	
	public static void main(String[] args) {
		try {
			int success = 0;
			int fail = 0;
			int[] vals = {4,8,16,20};
			int idx = 0;
			int iter = 0;
			
			while(success < 4 || fail < 2){
				String pathname = "success="+success+"&fail="+fail+"&iter="+iter+".txt";
				if(idx == vals.length) idx = Util.randInt(0, vals.length-1);
				File f = generate(vals[idx],pathname);
				idx++;
				
				if(!Main.isUltrametric(f)) {
					fail++;
					System.out.println("Failure on iter="+iter);
				}
				else{
					System.out.println("Success on iter="+iter);
					success++;
				}
				
				iter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
