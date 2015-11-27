package ultrametricTree;

import java.io.File;

import util.Util;

public class Main {
	
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
		
		System.out.println(U.isUltrametric());
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
