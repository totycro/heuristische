package heur;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Greedy {
	
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/true);
	}
	
	private Problem problem;

	public Greedy(Problem problem) {
		this.problem = problem;
	}
	
	/**
	 * Executes the greedy construction heuristic.
	 * @return a solution
	 */
	public Solution execute() {
		Solution solution = new Solution( problem.getCitiesNum() );
		
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
				
				
				// get all games
				List<Solution.Game> games = solution.getPossibleGames(cur, round, problem.consecHome, problem.consecRoad);
				System.err.println("found "+games.size()+" opponents");
				
				if (games.isEmpty()) {
					System.err.println("Can't find an opponent for " + cur  + " in " + round +". Giving up.");
					return solution;
				}
				
				// greedy: get closest city
				Solution.Game chosenGame = null;
				
				int curMin = Integer.MAX_VALUE;
				for (Solution.Game game : games) {
					int travelDistance = 0;
					int gameLocation = game.getLocation();
					log.info("check game: " +game);
					for (int player : game.getPlayers()) {
						// assume players start at home
						int lastLoc = (round == 0) ? player : solution.getLocationOfGame(player, round-1);
						travelDistance += problem.getDistance(lastLoc, gameLocation);
						if (lastLoc == gameLocation) {
							log.info("player "+player+" can stay");
						} else {
							log.info("player "+player+" would have to move from "+lastLoc+ " to "+gameLocation+", which costs: "+ problem.getDistance(lastLoc, gameLocation));
						}
					}
					
					// minimum
					if (travelDistance < curMin) {
						curMin = travelDistance;
						chosenGame = game;
						log.info("Found cheaper game: "+game +"; cost: " + travelDistance);
					}
				}
				
				int other = chosenGame.getOther(cur);
				curCities.remove(Integer.valueOf(other));
				
				solution.addGame(chosenGame, round);
			}
		}
		
		return solution;
	}
	

}
