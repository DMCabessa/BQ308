package util;

public class SequenceGenerator {
	private int min, max, pairs;
	private char[] dictionary = {'A','R','N','D','C','Q','E','G','H','I','L','K','M','F','P','S','T','W','Y','V','B','Z','X','*'};
	
	public SequenceGenerator(int min, int max, int pairs){
		this.min = min;
		this.max = max;
		this.pairs = pairs;
	}
	
	public void generate(){
		for(int i = 0; i < pairs; i++){
			int slen = Util.randInt(min, max);
			int tlen = Util.randInt(min, max);
			
			String s = "";
			String t = "";
			for(int j = 0; j < slen; j++){
				int pos = Util.randInt(0, dictionary.length-2);
				s = s + dictionary[pos];
			}
			
			for(int j = 0; j < tlen; j++){
				int pos = Util.randInt(0, dictionary.length-2);
				t = t + dictionary[pos];
			}
			
			String pair = s + " " + t;
			//FileHandler.write(pair, FileHandler.INPUT2);
		}
	}
	
	public static void main(String[] args) {
		SequenceGenerator sg = new SequenceGenerator(70, 200, 1);
		sg.generate();
	}
	
}
