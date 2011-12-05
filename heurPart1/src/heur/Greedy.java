package heur;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Greedy {
	
	private final static Logger log = Logger.getLogger(Greedy.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/false);
	}
	
	private Problem problem;

	public Greedy(Problem problem) {
		this(problem, false);
	}
	public Greedy(Problem problem, boolean silenceLogger) {
		this.problem = problem;
		if (silenceLogger) {
			log.setLevel( Level.OFF );
		}
	}
	
	/**
	 * Executes the greedy construction heuristic.
	 * @return a solution
	 */
	public Solution execute() {
		return execute(0);
	}
	public Solution execute(long seed) {
		Random rand = new Random(seed);
		// rng for rng seeds
		
		for (int i=0;;i++) {
			long subSeed = rand.nextLong();
			Solution sol = doExecute( subSeed );
			if (sol != null) {
				System.err.println("seed: "+ subSeed );
				return sol;
			}
		}
	}
		
	private Solution doExecute(long seed) {
		Random rand = new Random(seed);
		Solution solution = new Solution( problem.getCitiesNum(), problem );
		
		HashSet<Integer> _cities = new HashSet<Integer>();
		for (int i=0; i<solution.getCitiesNum(); ++i) {
			_cities.add(i);
		}
		
		for (int round=0; round<solution.getRoundsNum(); ++round) {
			Set<Integer> curCities = (HashSet<Integer>) _cities.clone();
			log.warning("\nROUND: " + round);
			
			while (!curCities.isEmpty()) { // assign games until all cities are taken care of
				// chose one
				int which = rand.nextInt(curCities.size());
				int cur = Util.choose(curCities, which);
				log.warning("choose: " + cur);
				
				// get all games
				List<Solution.Game> games = solution.getPossibleGames(cur, round, problem.consecHome, problem.consecRoad);
				log.warning("found "+games.size()+" opponents");
				
				if (games.isEmpty()) {
					log.severe("Can't find an opponent for " + cur  + " in " + round );
					return null;
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