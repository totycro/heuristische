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
		
		int iterations = 1000;
		int ants = 15;
		
		long time = Calendar.getInstance().getTimeInMillis();
		
		Solution bestSol = null;
		
		ArrayList<Integer> bestSolValues = new ArrayList<Integer>();
		
		for (int iteration=0; iteration<iterations; ++iteration) {
			
			if (iteration == 2) {
				GreedyAnt.some_flag = true;
			}

			List<Solution> solutions = new ArrayList<Solution>();
			
			//System.err.println("aco it: "+iteration);

			for (int i=0; i<ants; i++) {
				//System.err.println("\tant: "+i+" of "+ants);
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
			
			double delta = 0.02;
			// update pheromones of 2 best ants
			// LESS MEANS BETTER
			for (int j=0; j<problem.pheromones.length; j++) {
				for (int k=0; k<problem.pheromones.length; k++) {
					if (j!=k) {
						problem.pheromones[j][k] += delta;
					}
				}
			}
			double update_importance = 1.0;
			for (int i=0; i<1; ++i) {
				update_importance -= 0.1;
				Solution sol = solutions.get(i);
				
				//System.err.println(sol);
				//System.err.println("before:");
				//proj1.printMatrix(problem.pheromones, new PrintWriter(System.err));
				
				for (int round = 0; round < sol.getRoundsNum(); round++) {
					for (int city = 0; city < sol.getCitiesNum(); city++) {
						
						//Solution.Game game = sol.getGameOfCityInRound(city, round);
						//System.err.println("game: " + game);
						
						/*
						
						int other = game.getOther(city);
						if (game.playAt(other)) { // away game
							other += sol.getCitiesNum();
						}
						int lastOther = -1;
						if (round == 0) {
							// we start at home
							lastOther = city;
						} else {
							Solution.Game lastGame = sol.getGameOfCityInRound(city, round-1);
							lastOther = lastGame.getOther(city);
							if (lastGame.playAt(lastOther)) { // away game
								lastOther += sol.getCitiesNum();
							}
						}
						*/
						//System.err.println("last: " + lastOther+ "; now: " +other);
						
						//System.err.print
						int[] indices = sol.getPheromoneMatrixIndices(round, city);
						// LESS MEANS BETTER
						problem.pheromones[indices[0]][indices[1]] -= delta * update_importance;
						//System.err.println("indices: " + indices[0]+ " " + indices[1]);
						/*
						 * old wrong code
						Solution.Game game = sol.getGameOfCityInRound(city, round);
							
						int lastLoc = (round == 0) ? city : sol.getLocationOfGame(city, round-1);
						int lastLoc = (round == 0) ? city : sol.getLocationOfGame(city, round-1);
						if (lastLoc == thisLoc) {
							continue;
						}
						
						if (lastLoc != city) { // away, this means upper values
							lastLoc += sol.getCitiesNum();
						}
						
						if (thisLoc != city) { // away, this means upper values
							thisLoc += sol.getCitiesNum();
						}
						System.err.println("last: " + lastLoc+ "; now: " +thisLoc);
						
						problem.pheromones[lastLoc][thisLoc] -= 0.02;
						*/
					}
				}
				//System.err.println("after:");
				//proj1.printMatrix(problem.pheromones, new PrintWriter(System.err));
				//System.exit(0);
			}
			
			if (iteration % 50 == 0) {
				System.err.print("\n");
				proj1.printMatrix(problem.pheromones, new PrintWriter(System.err));
				System.err.print("\n");
				//System.err.println ("new weight: " + GreedyAnt.dist_weight);
			}
			
			if (iteration % (int)(iterations/10) == 0) {
				GreedyAnt.dist_weight *= 0.85;
			}
			
			Solution bestSolHere = solutions.get(0);
			bestSolValues.add(bestSolHere.getCumulativeCost());
			
			if (iteration % 10 == 0) {
				System.err.println("best in iteration "+iteration+": "+bestSolHere.getCumulativeCost());
			}
			
			if (bestSol == null || bestSolHere.getCumulativeCost() < bestSol.getCumulativeCost()) {
				bestSol = bestSolHere;
			}
		}
		
		int x = 0;
		int partSize = bestSolValues.size()/20;
		for (int i=0; i<bestSolValues.size(); i++) {
			x += bestSolValues.get(i);
			
			if (i % partSize == 0) {
				System.err.println ( i/partSize + ": " + x/partSize );
				x = 0;
			}
			
			
		}
		
		
		long time2 = Calendar.getInstance().getTimeInMillis();
		System.err.println("took " + (time2-time));
		
		
		return bestSol;
		
	}

}
