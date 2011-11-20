package heur;

import java.util.*;
import java.lang.*;

/**
 * Simple interface to a solution
 * 
 * This representation is going to be slow, but it should provide nice accessors
 * for uncritical code. We could perhaps export it from this format to int[][]
 * at the few places where we really need it.
 * 
 * @author totycro
 * 
 */
public class Solution {

	/**
	 * A game in a round
	 */
	public static class Game {
		public int a, b;
		boolean playAtA;

		public Game(int a, int b, boolean playAtFirst) {
			this.a = a;
			this.b = b;
			this.playAtA = playAtFirst;
		}

		boolean contains(int x) {
			return a == x || b == x;
		}

		int getOther(int one) {
			if (a == one) {
				return b;
			}
			if (b == one) {
				return a;
			}
			return -1;
		}

		boolean playAt(int one) {
			return ((one == a) && playAtA) || (one != a && !playAtA);
		}
		
		int getLocation() {
			if (playAtA) {
				return a;
			} else {
				return b;
			}
		}

		List<Integer> getPlayers() {
			return Arrays.asList(this.a, this.b);
		}

		boolean conflict(Game other) {
			return this.a == other.a || this.a == other.b || this.b == other.a
					|| this.b == other.b;
		}

		@Override
		public String toString() {
			return "Game(" + a + "," + b + "," + playAtA + ")";
		}
	}

	private List<List<Game>> solution;
	private int n;

	public Solution(int n) {
		this.n = n;
		solution = new ArrayList<List<Game>>();
		for (int i = 0; i < getRoundsNum(); i++) {
			solution.add(new ArrayList<Game>());
		}
	}

	public void setDebugData() {
		for (int i = 0; i < getRoundsNum(); i++) {
			for (int j = 0; j < getGamesNum(); j++) {
				solution.get(i).add(new Game(j * 2, (j * 2) + 1, (j % 2) == 1));
			}
		}

	}

	public Game getGameOfCityInRound(int city, int round) {
		if (round < 0) {
			return null;
		}
		for (Game game : getRound(round)) {
			if (game.contains(city)) {
				return game;
			}
		}
		return null;
	}

	public List<Game> getRound(int i) {
		if (i < 0) {
			return null;
		} else {
			return solution.get(i);
		}
	}

	public int getGamesNum() {
		return (int) (n / 2);
	}

	public int getCitiesNum() {
		return n;
	}

	public int getRoundsNum() {
		return (2 * n) - 2;
	}
	
	public boolean isComplete() {
		if (solution.size() != getRoundsNum()) {
			return false;
		}
		for( List<Game> round : solution  ) {
			if (round.size() != getGamesNum()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns solution in Trick's format
	 */
	@Override
	public String toString() {
		String s = "";
		String tmp = "";
		for (int i = 0; i < getCitiesNum(); i++) {
			s += getCityName(i) + "  ";
			tmp += "-  ";
		}
		s += "\n" + tmp + "\n";
		// rounds
		for (int round = 0; round < getRoundsNum(); round++) {
			for (int city = 0; city < getCitiesNum(); city++) {
				Game game = getGameOfCityInRound(city, round);
				if (game == null) {
					s += "   ";
				} else {
					int other = game.getOther(city);
					if (game.playAt(other)) {
						s += "@";
					}
					s += getCityName(other) + " ";
					if (!game.playAt(other)) {
						s += " ";
					}
				}
			}
			s += "\n";
		}
		return s;
	}

	private String getCityName(int i) {

		// TODO: city names:
		// ATL NYM PHI MON FLA PIT CIN CHI STL MIL HOU COL SF SD LA ARI
		char fst = new String("A").charAt(0);
		return new Character((char) (fst + i)).toString();
	}

	/**
	 * Returns possible games for a city in a round.
	 */
	public List<Game> getPossibleGames(int city, int round, int consecHome,
			int conseqRoad) {
		List<Game> possibleGames = new ArrayList<Game>();
		Set<Integer> usedHome = new HashSet<Integer>();
		Set<Integer> usedAbroad = new HashSet<Integer>();
		
		// check already played
		for (int i = 0; i < round; i++) {
			// System.err.println("city : " + city + "; round: " + round);
			Game game = getGameOfCityInRound(city, i);
			if (game.playAt(city)) {
				usedHome.add(game.getOther(city));
			} else {
				usedAbroad.add(game.getOther(city));
			}
		}
		Set<Integer> candidates = new HashSet<Integer>();
		for (int i = 0; i < getCitiesNum(); i++) {
			candidates.add(i);
		}
		candidates.remove(city);
		// drop games of current round
		for (Game game : getRound(round)) {
			for (int player : game.getPlayers()) {
				candidates.remove(player);
			}
		}

		// check consecutive constraints
		boolean canPlayHome = canPlayAt(city, round, true, consecHome, conseqRoad);
		boolean canPlayAbroad = canPlayAt(city, round, false, consecHome, conseqRoad);

		// select an unused, current free team
		for (int candidate : candidates) {
			if (!usedHome.contains(candidate)) {
				// play at home against candidate
				if (canPlayHome && canPlayAt(candidate, round, false, consecHome, conseqRoad)) {
					// NOTE: this is not optimal here, we could have cut a lot of games if we did it earlier
					possibleGames.add(new Game(city, candidate, /* atCity= */true));
				}
			}

			if (!usedAbroad.contains(candidate)) {
				if (canPlayAbroad && canPlayAt(candidate, round, true, consecHome, conseqRoad)) {
					possibleGames.add(new Game(city, candidate, /* atCity= */false));
				}
			}
		}

		return possibleGames;
	}

	/**
	 * Check consecutive games restrictions 
	 * @param home: whether to check for home
	 */
	private boolean canPlayAt(int city, int round, boolean home, int consecHome, int conseqRoad) {
		int roundBeforeLast = round - 1;
		// check relevant games
		// consecHome = 3  => if last 3 games were at home, we can't play at home any more
		for (int i=roundBeforeLast; i > (roundBeforeLast - (home ? consecHome : conseqRoad)); i--) {
			if (i < 0) { // we want back before the history has started, therefore the constraint can't be already violated
				return true;
			}
			Game game = getGameOfCityInRound(city, i);
			boolean playedAtHome = game.playAt(city);
			if ( (!playedAtHome && home) || ( playedAtHome && !home) ) {
				return true; // game was not where we want to play now
			}
		}
		return false; // condition above not hit, so all were at the bad location
	}

	public void addGame(Game game, int round) {
		// sanity
		for (Game g : getRound(round)) {
			if (game.conflict(g)) {
				throw new RuntimeException(
						"two games at the same time; round: " + round
								+ "; game1: " + game + "; game2: " + g);
			}
		}
		solution.get(round).add(game);
	}
	
	public void dropRound(int round) {
		solution.get(round).clear();
	}
	
	public int getLocationOfGame(int city, int round) {
		return getGameOfCityInRound(city, round).getLocation();
	}
	
	public int getCurrentRound() { // i.e. last round that has at least one game
		for (int i=0; i<solution.size(); i++) {
			if (solution.get(i).isEmpty()) {
				return i > 0 ? i - 1 : 0;
			}
		}
		return solution.size(); // already full
		
	}
	
	public int getCumulativeCost(Problem problem) {
		int cost = 0;
			
		for (int round=0; round<getCurrentRound(); ++round) {
			/*
			if (round == 1) {
				System.err.println("first round cost: "+ cost);
			}
			*/
			for (int player=0; player<getCitiesNum(); player++) {
				int lastLoc = (round == 0) ? player : getLocationOfGame(player, round-1);
				int gameLocation = getLocationOfGame(player, round);
				cost += problem.getDistance(lastLoc, gameLocation);
			}
		}
		// back home
		for (int player=0; player<getCitiesNum(); player++) {
			int gameLocation = getLocationOfGame(player, getCurrentRound()-1);
			cost += problem.getDistance(gameLocation, player);
		}
		return cost;
	}

}
