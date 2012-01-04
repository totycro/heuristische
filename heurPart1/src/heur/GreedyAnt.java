package heur;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GreedyAnt {
	
	private final static Logger log = Logger.getLogger(GreedyAnt.class.getName());
	static  {
		Util.setupLogger(log, /*on=*/false);
	}
	
	private Problem problem;

	public GreedyAnt(Problem problem) {
		//this(problem, false);
		this.problem = problem;
	}
	/*
	public GreedyAnt(Problem problem, boolean silenceLogger) {
		this.problem = problem;
		if (silenceLogger) {
			log.setLevel( Level.OFF );
		}
	}
	*/
	
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
			// more stable:
			Solution sol = doExecute( subSeed );
			// better results:
			//Solution sol = doExecuteUnstable( subSeed );
			if (sol != null) {
				//System.err.println("found at "+i+ "; quality: "+sol.getCumulativeCost());
				
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
			//Set<Integer> curCities = (HashSet<Integer>) _cities.clone();
			log.warning("ROUND: " + round);
			
			
			// get possible games for each city
			final HashMap<Integer, List<Solution.Game> > possibleGames = new HashMap<Integer, List<Solution.Game> >();
			
			for (int i=0; i<solution.getCitiesNum(); ++i) {
				List<Solution.Game> games = solution.getPossibleGames(i, round, problem.consecHome, problem.consecRoad);
				possibleGames.put(i, games);
			}
			
			while (!possibleGames.isEmpty()) { // assign games until all cities are taken care of
				
				// sort cities by no of possible games
				List<Integer> cities = new ArrayList<Integer>(possibleGames.keySet());
				Collections.sort(cities, new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						return new Integer(possibleGames.get(o1).size()).compareTo(possibleGames.get(o2).size());
					}
				});
				
				// choose any of the ones with least possibilities
				int choices = possibleGames.size();
				int min = possibleGames.get( cities.get( 0 ) ).size();
				if (min == 0) {
					choices = 1; // use first one and fail
				} else if (min == 1) {
					// use first class
					for (int i=0; i<cities.size(); i++) { 
						if (possibleGames.get( cities.get( i ) ).size() != min) {
							choices = i;
							break;
						}
					}
				} else { // no forced decision, choose among good ones
					//choices /= 2;
					// choices = Math.max(1, choices); // check not needed for division by 2
				}
								
				int which = rand.nextInt(choices); // choices is exclusive upper bound
				int cur = cities.get(which);
				
				//log.warning("choose: " + cur);
				
				// get all games
				List<Solution.Game> games = possibleGames.get(cur);
				//log.warning("found "+games.size()+" opponents");
				
				if (games.isEmpty()) {
					log.severe("Can't find an opponent for " + cur  + " in " + round );
					return null;
				}
				
				// ANT:
				// Now we have to make a decision for this city.
				
				// calculate auxiliary values
				Map<Solution.Game, Double> dist_quotient = new HashMap<Solution.Game, Double>(); // value for distance, in [0,1]
				Map<Solution.Game, Double> pheromone_quotient = new HashMap<Solution.Game, Double>(); // desirability
				
				// dist_quot = dist_to_city / max_dist_of_possible_games
				{
					int max_dist = 0;
					Map<Solution.Game, Integer> distances = new HashMap<Solution.Game, Integer>(); 
					for (Solution.Game game : games) {
						int travelDistance = 0;
						int gameLocation = game.getLocation();
						for (int player : game.getPlayers()) {
							//  players start at home
							//int lastLoc = lastLocations.get(player);
							int lastLoc;
							if (round == 0) {
								lastLoc = player;
							} else {
								lastLoc = solution.getLocationOfGame(player, round-1);
							}
							travelDistance += problem.getDistance(lastLoc, gameLocation);
						}
						if (travelDistance > max_dist) {
							max_dist = travelDistance;
						}
						distances.put(game, travelDistance);
					}
					
					for (Map.Entry<Solution.Game, Integer> entry : distances.entrySet()) {
						dist_quotient.put(entry.getKey(), new Double(entry.getValue()) / max_dist);
					}
				}
				
				// pheromone_quot: just take value from table
				for (Solution.Game game : games) {
					int[] indices = solution.getPheromoneMatrixIndices(round, cur, game);
					pheromone_quotient.put(game, problem.pheromones[indices[0]][indices[1]] );
				}
				
				// take min
				
				double minRating = Double.MAX_VALUE;
				double dist_weight = 1.0;
				double pheromone_weight = 10.0;
				
				Solution.Game chosenGame = null;
				for (Solution.Game game : games) {
					double rating =
							(dist_quotient.get(game) * dist_weight) +
							(pheromone_quotient.get(game) * pheromone_weight);
					
					
					if (rating < minRating) {
						chosenGame = game;
						rating = minRating;
					}
				}
					
				Integer other = chosenGame.getOther(cur);
				// remove the two cities, also all other games that would contain them
				possibleGames.remove(cur);
				possibleGames.remove(other);
				
				for (List<Solution.Game> gamelist : possibleGames.values()) {
					Iterator<Solution.Game> iter = gamelist.iterator();
					// iterator for safe list removal during iteration
					while (iter.hasNext()) {
						Solution.Game game = iter.next();
						if (game.contains(cur) || game.contains(other)) {
							iter.remove();
						}
					}
				}
				
				solution.addGame(chosenGame, round);
				//log.info("game: " +chosenGame);
			}
			
		}
		
		return solution;
	}	
	
}
