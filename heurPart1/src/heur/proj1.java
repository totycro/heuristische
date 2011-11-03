package heur;

import javax.swing.JTable.PrintMode;

public class proj1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length < 2) {
			System.err.println("USAGE: <program> input [ -g | -n1 | -n2 ]");
			System.err.println("\t-g: greedy construction");
			System.err.println("\t-n1: neighborhood search 1");
			System.err.println("\t-n2: neighborhood search 2");
			System.exit(1);
		}
		// TODO: max consecutive home/abroad parameter
		
		
		//String data = "data8.txt";
		String data = args[0];
		TxtFile txf = new TxtFile(data);
		int[][] distM = txf.getDist();
		int n = txf.getN();
		System.err.println("read distances: ");
		printMatrix(distM);
		//
		
		// we always need greedy
		Solution sol = new Greedy(distM).execute();
		
		if (args[1].equals("-g")) {
			System.err.println("\ngreedy solution: \n"+sol);
		} else if (args[1].equals("-n1")) {
			// TODO
			System.err.println("\nn1 TODO");
		} else if (args[1].equals("-n2")) {
			// TODO
			System.err.println("\nn2 TODO");
		} else {
			System.err.println("invalid param: " + args[1]);
			System.exit(1);
		}
	}
	
	
	static private void printMatrix(int [][] M) {
		for (int i=0; i<M.length; ++i) {
			for (int j=0; j<M[0].length; ++j) {
				System.err.format("%6d", M[i][j]);
			}
			System.err.println("");
			
		}
	}

}
