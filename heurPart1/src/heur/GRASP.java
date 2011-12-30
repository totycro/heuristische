package heur;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

public class GRASP {
		
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/true);
	}
	
	private final static int GREEDY_TRIES = 200;
	
	private long seed;
	
	public GRASP(long seed) {
		this.seed = seed;
	}
	
	private static final boolean record_executions = false;
	
	public Solution execute(Problem problem, int iterations) {
		
		Greedy greedy = new Greedy(problem); //, /*silenceLogger=*/true);
		
		Random rand = new Random(seed);
		
		
		String filename = "/tmp/a.asdf";
		if (record_executions) {
				if (new File(filename).exists()) {
					new File(filename).delete();
				}
		}
				
		Solution bestSol = null;
		assert (iterations > 0);
		int oldCost = Integer.MAX_VALUE;
		for (int i=0; i<iterations; ++i) {
			System.err.println("\ngrasp iteration "+i);
			
			// greedy is fast, so try multiple times for a good solution and use best one
			SortedMap<Integer, Solution> greedySolutions = new TreeMap<Integer, Solution>();
			for (int j=0; j<GREEDY_TRIES; ++j) {
				Solution sol = greedy.execute( rand.nextLong() );
				greedySolutions.put(sol.getCumulativeCost() , sol);
			}
			
			Solution sol = greedySolutions.get( greedySolutions.firstKey() );
			
			// continue with vanilla grasp
			
			VND vnd = new VND();
			vnd.execute(sol);
			
			log.warning("new solution cost: " + sol.getCumulativeCost());
			int newCosts = sol.getCumulativeCost() ;
			System.err.println("new solution cost: " + newCosts);
			if(Util.protocolBoolean){
				Util.protocol += "new solution cost: " + newCosts+"\n";
			}
			
			// code to record all grasp iterations
			if (record_executions) {
			try {
					
				FileWriter fstream = new FileWriter(filename, true);
				fstream.write(newCosts + "\n");
				fstream.flush();
				fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
				
			if (newCosts < oldCost) {
				oldCost = newCosts;
				System.err.println("is new min");
				if(Util.protocolBoolean){
					Util.protocol += "is new min"+"\n";
				}
				bestSol = sol;
			} else {
				System.err.println("is worse, cur min: "+oldCost);
				if(Util.protocolBoolean){
					Util.protocol += "is worse, cur min: "+oldCost+"\n";
				}
			}
		}
		
		return bestSol;
	}

}
