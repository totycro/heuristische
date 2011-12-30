package heur;

import java.io.PrintWriter;
import java.util.*;

public class ACO {
	
	private Problem problem;
	
	public ACO(Problem problem) {
		this.problem = problem;
	}
	
	public Solution execute() {
		GreedyAnt g = new GreedyAnt(problem);
		
		// TODO: loop
		int iterations = 100;
		int ants = 10;
		
		long time = Calendar.getInstance().getTimeInMillis();
		
		Solution bestSol = null;
		
		for (int iteration=0; iteration<iterations; ++iteration) {

			List<Solution> solutions = new ArrayList<Solution>();

			for (int i=0; i<ants; i++) {
				Solution sol = g.execute( i + (ants * iteration) ); // an
				solutions.add(sol);
			}

			// sort ants
			Collections.sort(solutions, new Comparator<Solution>() {
				@Override
				public int compare(Solution o1, Solution o2) {
					return new Integer(o1.getCumulativeCost()).compareTo(o2.getCumulativeCost());
				}
			});

			// first ant is best ant

			//for (int i=0; i<solutions.size(); ++i) { System.err.println(i + ": " + solutions.get(i).getCumulativeCost()); }
			
			// update pheromones of 2 best ants
			// LESS MEANS BETTER
			for (int j=0; j<problem.pheromones.length; j++) {
				for (int k=0; k<problem.pheromones.length; k++) {
					if (j!=k) {
						problem.pheromones[j][k] += 0.02;
					}
				}
			}
			for (int i=0; i<2; ++i) {
				Solution sol = solutions.get(i);
				
			
				
				for (int round = 0; round < sol.getRoundsNum(); round++) {
					for (int city = 0; city < sol.getCitiesNum(); city++) {
						Solution.Game game = sol.getGameOfCityInRound(city, round);
						int lastLoc = (round == 0) ? city : sol.getLocationOfGame(city, round-1);
						int thisLoc = game.getLocation();
						if (lastLoc == thisLoc) {
							continue;
						}
						
						if (lastLoc != city) { // away, this means upper values
							lastLoc += sol.getCitiesNum();
						}
						if (thisLoc != city) { // away, this means upper values
							thisLoc += sol.getCitiesNum();
						}
						
						problem.pheromones[lastLoc][thisLoc] -= 0.02;
					}
				}
			}
			
			if (iteration % 10 == 0) {
				System.err.print("\n");
				proj1.printMatrix(problem.pheromones, new PrintWriter(System.err));
				System.err.print("\n");
			}
			
			Solution bestSolHere = solutions.get(0);
			System.err.println("best in iteration "+iteration+": "+bestSolHere.getCumulativeCost());
			if (bestSol == null || bestSolHere.getCumulativeCost() < bestSol.getCumulativeCost()) {
				bestSol = bestSolHere;
			}
		}
		
		
		long time2 = Calendar.getInstance().getTimeInMillis();
		System.err.println("took " + (time2-time));
		
		
		return bestSol;
		
	}

}
