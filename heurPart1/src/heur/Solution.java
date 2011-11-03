package heur;

import java.util.*;
import java.lang.*;
		
/**
 * Simple interface to a solution
 * 
 * This representation is going to be slow, but it should provide nice accessors for uncritical code. 
 * We could perhaps export it from this format to int[][] at the few places where we really need it.
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
			if (a == one) { return b; }
			if (b == one) { return a; }
			return -1;
		}
		boolean playAt(int one) {
			return ((one == a) && playAtA) || (one != a && !playAtA);
		}
		List<Integer> getPlayers() {
			return Arrays.asList(this.a, this.b);
		}
		boolean conflict(Game other) {
			return this.a == other.a || this.a == other.b || this.b == other.a || this.b == other.b; 
		}
		@Override
		public String toString() {
			return "Game("+a+","+b+","+playAtA+")";
		}
	}
	
	private List< List< Game > > solution;
	private int n;
	
	public Solution(int n) {
		this.n = n;
		solution = new ArrayList< List< Game > >();
		for (int i=0; i<getRoundsNum();  i++) {
			solution.add( new ArrayList<Game>() );
		}
	}
	
	public void setDebugData() {
		for (int i=0; i<getRoundsNum();  i++) {
			for (int j=0; j<getGamesNum(); j++) {
				solution.get(i).add(new Game(j*2, (j*2) + 1, (j%2)==1));
			}
		}
		
	}
	
	public Game getGameOfCityInRound(int city, int round) {
		for (Game game : getRound(round)) {
			if (game.contains(city)) {
				return game;
			}
		}
		return null;
	}
	
	public List<Game> getRound(int i) {
		return solution.get(i);
	}
	
	public int getGamesNum()  { return (int) (n/2); }
	public int getCitiesNum() { return n; }
	public int getRoundsNum() { return (2*n)-2; }
	
	
	/**
	 * Returns solution in Trick's format
	 */
	@Override
	public String toString() {
		String s = "";
		String tmp = "";
		for (int i=0; i<getCitiesNum(); i++) {
			s += getCityName(i) + "  ";
			tmp += "-  ";
		}
		s += "\n" + tmp + "\n";
		// rounds
		for (int round=0; round<getRoundsNum(); round++) {
			for (int city=0; city<getCitiesNum(); city++) {
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
		return new Character((char)(fst + i)).toString() ;
	}

	/**
	 * Returns one game opponent for city
	 */
	public Game getFirstPossibleGame(int city, int round) {
		Set<Integer> usedHome = new HashSet<Integer>();
		Set<Integer> usedAbroad = new HashSet<Integer>();
		// check already played
		for (int i=0; i<(round); i++) {
			//System.err.println("city : " + city + "; round: " + round);
			Game game = getGameOfCityInRound(city, i);
			if (game.playAt(city)) {
				usedHome.add( game.getOther(city) );
			} else {
				usedAbroad.add( game.getOther(city) );
			}
		}
		Set<Integer> candidates = new HashSet<Integer>();
		for (int i=0; i<getCitiesNum(); i++) {
			candidates.add(i);
		}
		candidates.remove(city);
		// drop games of current round
		for (Game game : getRound(round)) {
			for (int player : game.getPlayers()) {
				candidates.remove(player);
			}
		}
		
		// select an unused, current free team
		for (int candidate : candidates) {
			if (! usedHome.contains(candidate) ) {
				// play at home against candidate
				return new Game(city, candidate, true);
			}
			
			if (! usedAbroad.contains(candidate) ) {
				return new Game(city, candidate, false);
			}
		}
		// Screw you guys, i'm going home.
		return null;
	}

	public void addGame(Game game, int round) {
		// sanity
		for (Game g : getRound(round)) {
			if (game.conflict(g)) {
				throw new RuntimeException("two games at the same time; round: "+round+"; game1: "+game+"; game2: "+g);
			}
		}
		solution.get(round).add(game);
	}
	

}
