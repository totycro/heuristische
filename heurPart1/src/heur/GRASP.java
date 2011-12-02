package heur;

import java.util.Random;
import java.util.logging.Logger;

public class GRASP {
		
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/true);
	}
	
	private long seed;
	
	public GRASP(long seed) {
		this.seed = seed;
	}
	
	public Solution execute(Problem problem, int iterations) {
		
		Greedy greedy = new Greedy(problem); //, /*silenceLogger=*/true);
		
		Random rand = new Random(seed);
		
		Solution bestSol = null;
		assert (iterations > 0);
		int oldCost = Integer.MAX_VALUE;
		for (int i=0; i<iterations; ++i) {
			System.err.println("\ngrasp iteration "+i);
			
			Solution sol = greedy.execute( rand.nextLong() );
			
			VND vnd = new VND();
			vnd.execute(sol);
			
			log.warning("new solution cost: " + sol.getCumulativeCost());
			int newCosts = sol.getCumulativeCost() ;
			System.err.println("new solution cost: " + newCosts);
			if (newCosts < oldCost) {
				oldCost = newCosts;
				System.err.println("is new min");
				bestSol = sol;
			} else {
				System.err.println("is worse, cur min: "+oldCost);
			}
		}
		
		return bestSol;
	}

}
