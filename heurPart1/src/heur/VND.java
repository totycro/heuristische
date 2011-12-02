package heur;

import java.util.logging.Logger;

public class VND {
		
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/true);
	}
	
	
	public VND() {
		// TODO: neighborhoods + their next function (next/best)
		
	}
	
	public Solution execute(Solution sol) {
		int i=0;
		int iterations = 2;
		
		Neighborhood nh = new Neighborhood(sol);
		
		while (i<iterations) {
			
			int oldCost = sol.getCumulativeCost();
			
			if (i==0) {
				nh.neighborhoodGames(1);
			} else if (i==1) {
				nh.neighborhoodRounds(1);
			}
			
			if (sol.getCumulativeCost() < oldCost) {
				log.warning("found better solution");
				i = 0;
			} else {
				log.warning("no better solution found in iteration "+i);
				i++;
			}
		}
		
		return sol;
	}

}
