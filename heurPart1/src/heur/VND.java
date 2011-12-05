package heur;

import java.util.logging.Logger;

public class VND {
		
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/true);
	}
	
	
	public VND() {
		
	}
	
	public Solution execute(Solution sol) {
		int i=0;
		int iterations = 2;
		
		Neighborhood nh = new Neighborhood(sol);
		
		while (i<iterations) {
			
			int oldCost = sol.getCumulativeCost();
			
			if (i==0) {
				nh.neighborhoodRounds(2,2,sol.getRoundsNum());
			} else if (i==1) {
				nh.neighborhoodGames(2,1,3);
				//nh.neighborhoodGames(1,5,6);
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
