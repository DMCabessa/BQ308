package reversionSort.module1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
	public final static File INPUT = new File("input.txt");
	
	public static List<List<Integer>> read(){
		try {
			FileReader fr = new FileReader(INPUT);
			BufferedReader br = new BufferedReader(fr);
			List<List<Integer>> result = new ArrayList<List<Integer>>();
			
			while(true){
				int length = Integer.parseInt(br.readLine());
				if(length == 0) break;
				
				String line = br.readLine();
				String[] vals = line.split(" ");
				List<Integer> nums = new ArrayList<Integer>();
				for(String val : vals){
					nums.add(Integer.parseInt(val));
				}
				
				result.add(nums);
			}
			
			br.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
