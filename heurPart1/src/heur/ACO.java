package heur;

import java.io.PrintWriter;
import java.util.*;

public class ACO {
	
	private Problem problem;
	
	public ACO(Problem problem) {
		this.problem = problem;
	}
	
	private static boolean do_greedy = false; // false means random ant
	
	
	public Solution execute() {
		Ant g;
		if (do_greedy) {
			g = new GreedyAnt(problem);
		} else {
			g = new RandomAnt(problem);
		}
		
		int iterations = -1;
		int ants = -1;
		
		if (do_greedy) {
			iterations = 500;
			ants = 15;
		} else { // random
			iterations = 500;
			ants = 100;
		}
		
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
				sol.consecHome = problem.consecHome;
				sol.conseqRoad = problem.consecRoad;
				solutions.add(sol);
			}

			// sort ants
			if (do_greedy) {
				Collections.sort(solutions, new Comparator<Solution>() {
					@Override
					public int compare(Solution o1, Solution o2) {
						return new Integer(o1.getCumulativeCost()).compareTo(o2.getCumulativeCost());
					}
				});
			} else {
				Collections.sort(solutions, new Comparator<Solution>() {
					@Override
					public int compare(Solution o1, Solution o2) {
						return new Integer(o1.getCumulativeCost() + o1.getPunishment()).compareTo(o2.getCumulativeCost() + o2.getPunishment());
					}
				});
			}

			// first ant is best ant

			//for (int i=0; i<solutions.size(); ++i) { System.err.println(i + ": " + solutions.get(i).getCumulativeCost()); }
			
			double delta = 0.01; //double delta = 0.01;
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
			int num_ants_allowed_to_set = -1;
			if (do_greedy) {
				num_ants_allowed_to_set = 3;
			} else {
				num_ants_allowed_to_set = 1;
			}
			for (int i=0; i<num_ants_allowed_to_set; ++i) {
				update_importance -= 0.2; //update_importance -= 0.2; 3 ameisen
				Solution sol = solutions.get(i);
				//Neighborhood nh = new Neighborhood(sol);
				//nh.neighborhoodRounds(1, 0, 3);
				//LOCAL SEARCH
				//System.err.println(sol);
				//System.err.println("before:");
				//proj1.printMatrix(problem.pheromones, new PrintWriter(System.err));
				if (do_greedy) {
					if(i == 0 ) {
						Neighborhood nh = new Neighborhood(sol);
						nh.neighborhoodGames(1, 0, 3);
						nh.neighborhoodRounds(1, 0, 3);
					}
				} else { // more local search for random
					if(i < 3 ) { // doesn't matter if only first ant sets
						Neighborhood nh = new Neighborhood(sol);
						nh.neighborhoodGames(1, 0, 3);
						nh.neighborhoodRounds(1, 0, 3);
					}
				}

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
			} // end for pheromone update
			
			//if (iteration % 50 == 0) {
			if (iteration % (int)(iterations/4) == 0) {
				System.err.print("\n");
				proj1.printMatrix(problem.pheromones, new PrintWriter(System.err));
				System.err.print("\n");
				//System.err.println ("new weight: " + GreedyAnt.dist_weight);
			}
			
			if (iteration % (int)(iterations/10) == 0) {
				GreedyAnt.dist_weight *= 0.85;
			}
			
			// PROTOKOLL:
			
			Solution bestSolHere = null;
			//System.err.println("best invalid: " + solutions.get(0));
			for (int i=0; i < solutions.size(); i++) {
				if (do_greedy || solutions.get(i).checkConstraint() == false) {
					bestSolHere = solutions.get(i);
					bestSolValues.add(bestSolHere.getCumulativeCost());
					break;
				}
			}
			

			if (bestSolHere == null) {
				if (iteration % 10 == 0) {
					System.err.println("best in iteration: no valid sol"); 
				}
			} else {
				if (iteration % 10 == 0) {
					System.err.println("best in iteration "+iteration+": "+bestSolHere.getCumulativeCost() + "  checkContraints: " + bestSolHere.checkConstraint());
				}

				if (bestSol == null || bestSolHere.getCumulativeCost() < bestSol.getCumulativeCost()) {
					bestSol = bestSolHere;
				}
			}
		} // big loop end

		Neighborhood nh = new Neighborhood(bestSol);
		nh.neighborhoodGames(1, 0, 4);
		nh.neighborhoodRounds(1, 0, 4);

		int x = 0;
		if (bestSolValues.size() > 0) {
			int partSize = bestSolValues.size()/20;
			partSize = Math.max(partSize, 1);
			for (int i=0; i<bestSolValues.size(); i++) {
				x += bestSolValues.get(i);

				if (i % partSize == 0) {
					System.err.println ( i/partSize + ": " + x/partSize );
					x = 0;
				}
			}
		}
		
		
		long time2 = Calendar.getInstance().getTimeInMillis();
		System.err.println("took " + (time2-time));
		
		
		return bestSol;
		
	}

}
