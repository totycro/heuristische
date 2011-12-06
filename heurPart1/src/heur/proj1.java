package heur;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class proj1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 5) {
			System.err.println("USAGE: <program> input { -g | -n1 | -n2 | -vnd | -grasp } ch cr param1");
			System.err.println("\t-g: greedy construction");
			System.err.println("\t-n1: neighborhood search 1");
			System.err.println("\t-n2: neighborhood search 2");
			System.err.println("\t-vnd: variable neighborhood descent");
			System.err.println("\t-grasp: well, do grasp.");
			System.err.println("\tch: max consecutive home");
			System.err.println("\tcr: max consecutive road");
			System.err.println("\tparam1");
			System.exit(1);
		}
		
		//String data = "data8.txt";
		String data = args[0];
		TxtFile txf = new TxtFile(data);
		int[][] distM = txf.getDist();
		//int n = txf.getN();
		System.err.println("read distances: ");
		printMatrix(distM, new PrintWriter(System.out));
		
		int consec_home = Integer.parseInt(args[2]);
		int consec_road = Integer.parseInt(args[3]);
		
		Problem problem = new Problem(distM, consec_home, consec_road);
		
		// we always need greedy
		Greedy greedy = new Greedy(problem);
		Solution sol = greedy.execute();
			
		if (!sol.isComplete()) {
			System.err.println("\ngreedy failed, part of solution: \n"+sol);
			try {
				new PrintWriter( new FileWriter("solution.txt") ).println(sol.toString());
			} catch (IOException e) {
				e.printStackTrace(); 
			}
			System.exit(1);
		}
		
		if (args[1].equals("-g")) {
			System.err.println("\ngreedy solution: \n"+sol);
			System.err.println("all costs: " +  sol.getCumulativeCost() );
			if(Util.protocolBoolean){
				Util.protocol += "\ngreedy solution: \n"+sol;
				Util.protocol += "all costs: " +  sol.getCumulativeCost() +"\n";
			}
		} else if (args[1].equals("-n1")) {
			int neigh = Integer.parseInt( args[4] );
			System.out.println("Neighborhood "+neigh);
			System.err.println("\ngreedy solution: \n"+sol);
			System.err.println("all costs: " +  sol.getCumulativeCost() );
			if(Util.protocolBoolean){
				Util.protocol += "\ngreedy solution: \n"+sol;
				Util.protocol += "all costs: " +  sol.getCumulativeCost() +"\n";
			}
			Neighborhood n1 = new Neighborhood(sol);
			//n1.neighborhoodRounds(neigh,2,sol.getRoundsNum());
			n1.neighborhoodRounds(neigh,1,4);
			System.err.println("\n1: \n"+sol);
			if(Util.protocolBoolean){
				Util.protocol += "\n1: \n"+sol;
			}
		} else if (args[1].equals("-n2")) {
			int neigh = Integer.parseInt( args[4] );
			System.out.println("Neighborhood "+neigh);
			System.err.println("\ngreedy solution: \n"+sol);
			System.err.println("all costs: " +  sol.getCumulativeCost() );
			//
			if(Util.protocolBoolean){
				Util.protocol += "\ngreedy solution: \n"+sol;
				Util.protocol += "all costs: " +  sol.getCumulativeCost() +"\n";
			}
			//
			Neighborhood n2 = new Neighborhood(sol);
			n2.neighborhoodGames(neigh,0,5);
			if(Util.protocolBoolean){
				Util.protocol += "\n2: \n"+sol;
			}
			//
			System.out.println("\nneigh. solution: \n"+sol);
		} else if (args[1].equals("-n3")) {
			//
			//
			int neigh = Integer.parseInt( args[4] );
			System.out.println("Neighborhood "+neigh);
			System.err.println("\ngreedy solution: \n"+sol);
			System.err.println("all costs: " +  sol.getCumulativeCost() );
			//
			if(Util.protocolBoolean){
				Util.protocol += "\ngreedy solution: \n"+sol;
				Util.protocol += "all costs: " +  sol.getCumulativeCost() +"\n";
			}
			//
			NeighborhoodMerg2 n3 = new NeighborhoodMerg2(sol);
			n3.neighborhoodMerg(neigh,0,5);
			if(Util.protocolBoolean){
				Util.protocol += "\n2: \n"+sol;
			}
			//
			System.out.println("\nneigh. solution: \n"+sol);
			//
		} else if (args[1].equals("-vnd")) {
			System.err.println("\ngreedy solution: \n"+sol);
			System.err.println("all costs: " +  sol.getCumulativeCost() );
			//
			if(Util.protocolBoolean){
				Util.protocol += "\ngreedy solution: \n"+sol;
				Util.protocol += "all costs: " +  sol.getCumulativeCost() +"\n";
			}
			//
			VND vnd = new VND();
			sol = vnd.execute(sol);
			System.out.println("\nvnd solution: \n"+sol);
			if(Util.protocolBoolean){
				Util.protocol += "\nvnd: \n"+sol;
			}
		} else if (args[1].equals("-grasp")) {
			GRASP grasp = new GRASP( (long) (42*1337 / Math.PI) );
			int iterations = Integer.parseInt( args[4] );
			sol = grasp.execute(problem, iterations);
			System.out.println("\ngrasp solution: \n"+sol);
			if(Util.protocolBoolean){
				Util.protocol += "\ngrasp solution: \n"+sol;
			}
		} else {
			System.err.println("invalid param: " + args[1]);
			System.exit(1);
		}
		
		System.out.println("all costs: " +  sol.getCumulativeCost() );
		
		try {
			PrintWriter p = new PrintWriter( new FileWriter("solution.txt") );
			p.println(sol.toString());
			p.flush();
			p.close();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		if(Util.protocolBoolean){
			try {
				PrintWriter p = new PrintWriter( new FileWriter("protocol.txt") );
				p.println(Util.protocol);
				p.flush();
				p.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}
	
	static private void printMatrix(int [][] M, PrintWriter out) {
		for (int i=0; i<M.length; ++i) {
			for (int j=0; j<M[0].length; ++j) {
				out.format("%6d", M[i][j]);
			}
			out.println();
		}
		out.flush();
	}

}
