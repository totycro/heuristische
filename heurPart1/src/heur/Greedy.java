package heur;

import java.util.HashSet;
import java.util.Set;

public class Greedy {
	
	private int[][] distM;
	
	public Greedy(int[][] distM) {
		this.distM = distM;
	}
	
	/**
	 * Executes the greedy construction heuristic.
	 * @return a solution
	 */
	public Solution execute() {
		Solution solution = new Solution(getN());
		
		HashSet<Integer> _cities = new HashSet<Integer>();
		for (int i=0; i<solution.getCitiesNum(); ++i) {
			_cities.add(i);
		}
		
		for (int round=0; round<solution.getRoundsNum(); ++round) {
			Set<Integer> curCities = (HashSet<Integer>) _cities.clone();
			System.err.println("\nROUND: " + round);
			
			while (!curCities.isEmpty()) { // assign games until all cities are taken care of
				// chose one
				int cur = Util.choose(curCities);
				System.err.println("choose: " + cur);
				
				// TODO: max consecutive home/abroad parameter
				
				// greedy
				Solution.Game game = solution.getFirstPossibleGame(cur, round);
				
				if (game == null) { 
					System.err.println("Can't find an opponent for " + cur  + " in " + round +". Giving up.");
					return solution;
				}
				
				int other = game.getOther(cur);
				curCities.remove(Integer.valueOf(other));
				
				solution.addGame(game, round);
			}
		}
		
		return solution;
	}
	
	private int getN() {
		return distM.length;
	}
	

}
