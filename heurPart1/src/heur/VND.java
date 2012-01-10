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
		int iterations = 5;
		
		Neighborhood nh = new Neighborhood(sol);
		
		while (i<iterations) {
			
			int oldCost = sol.getCumulativeCost();
			
			int bestNext = 2;
			if (i==0) {
				//nh.neighborhoodRounds(2,2,4);
				nh.neighborhoodRounds(bestNext,2,3);
			} else if (i==1) {
				nh.neighborhoodGames(bestNext,1,2);
				//nh.neighborhoodGames(2,1,3);
			} else if (i==2) {
				nh.neighborhoodGames(bestNext,1,2);
			} else if (i==3) {
				nh.neighborhoodRounds(bestNext,3,4);
			} else if (i==4) {
				nh.neighborhoodGames(bestNext,2,3);
			} else if (i==5) {
				nh.neighborhoodTeams(1);
			} else if (i==6) {
				nh.neighborhoodTeams(2);
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
