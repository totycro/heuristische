package heur;

import java.util.Random;
import java.util.logging.Logger;

public class GRASP {
		
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/false);
	}
	
	private long seed;
	
	public GRASP(long seed) {
		this.seed = seed;
	}
	
	public Solution execute(Problem problem, int iterations) {
		
		Greedy greedy = new Greedy(problem, /*silenceLogger=*/true);
		
		Random rand = new Random(seed);
		
		Solution bestSol = null;
		assert (iterations > 0);
		for (int i=0; i<iterations; ++i) {
			log.info("grasp iteration "+i);
			
			Solution sol = greedy.execute( rand.nextLong() );
			
			localSearch(sol);
			
			// TODO: if better:
			bestSol = sol;
		}
		
		return bestSol;
	}

	private void localSearch(Solution sol) {
		// TODO
		
	}

}
