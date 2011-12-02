package heur;

import java.util.*;

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
		
		public boolean comparePlayers(int aOther, int bOther){
			return ((aOther==this.a)&&(bOther==this.b)) || ((aOther==this.b)&&(bOther==this.a));
		}
	}

	private List<List<Game>> solution;
	private int n;
	private Problem problem;
	int consecHome;
	int conseqRoad;
	
	public Solution(int n, Problem problem) {
		this.n = n;
		solution = new ArrayList<List<Game>>();
		for (int i = 0; i < getRoundsNum(); i++) {
			solution.add(new ArrayList<Game>());
		}
		this.problem = problem;
	}
	
	public Solution(Solution sol){
		this.solution = sol.getSolution();
		this.n = sol.n;
	}
	
	public List<List<Game>> getSolution(){
		return solution;
	}
	
	public void setSolution(List<List<Game>> settingSol){
		this.solution=settingSol;
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
		this.consecHome = consecHome;
		this.conseqRoad = conseqRoad;
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

		// check constraints
		boolean canPlayHome = canPlayAt(city, round, true, consecHome, conseqRoad);
		boolean canPlayAbroad = canPlayAt(city, round, false, consecHome, conseqRoad);

		// select an unused, current free team
		for (int candidate : candidates) {
			if ( round > 0 && getGameOfCityInRound(city, round-1).contains(candidate)) { // repeat constraint
				continue;
			}
			if (!usedHome.contains(candidate)) {
				// play at home against candidate
				 // NOTE: this is not optimal here, we could have cut a lot of games if we did it earlier
				if (canPlayHome && canPlayAt(candidate, round, false, consecHome, conseqRoad)) {
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
	 * Check consecutive games and repeaters restrictions 
	 * @param home: whether to check for home
	 */
	public boolean canPlayAt(int city, int round, boolean home, int consecHome, int conseqRoad) {
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

	public int canPlayAtCnt(int city, int round, boolean home, int consecHome, int conseqRoad) {
		int roundBeforeLast = round - 1;
		// check relevant games
		// consecHome = 3  => if last 3 games were at home, we can't play at home any more
		int cnt = 0;
		for (int i=roundBeforeLast; i > (roundBeforeLast - (home ? consecHome : conseqRoad)); i--) {
			if (i < 0) { // we want back before the history has started, therefore the constraint can't be already violated
				break;
			}
			Game game = getGameOfCityInRound(city, i);
			boolean playedAtHome = game.playAt(city);
			if ( (!playedAtHome && home) || ( playedAtHome && !home) ) {
				break;
			}
			cnt = cnt+1;
		}
		return cnt; // condition above not hit, so all were at the bad location
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
	
	public int getCumulativeCost() {
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
	//////////////////////////////////////////////////////////////////////////////
	public int getSingeleCumulativeCost(int round, int otherRound) {
		int cost = 0;
		for (int player=0; player<getCitiesNum(); player++) {
			//
			int lastLoc = getLocationOfGame(player, otherRound);
			int gameLocation = getLocationOfGame(player, round);
			//
			cost += problem.getDistance(lastLoc, gameLocation);
		}
		return cost;
	}
	public ArrayList<Integer> getCumulativeCostOfRound( ) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int roundNum=0; roundNum<getRoundsNum(); roundNum++){
			if(roundNum==0){
				list.add(0);
			}
			else{
				list.add(getSingeleCumulativeCost( roundNum, roundNum-1));
			}
		}
		return list;
	}
	public ArrayList<Integer> getCumulativeCostOfAllRound( int round ) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int roundNum=0; roundNum<getRoundsNum(); roundNum++){
			list.add(getSingeleCumulativeCost( roundNum, round));
		}
		return list;
	}
	//////////////////////////////////////////////////////////////////////////////
	public List<List<Game>> deepCopy(){
		List<List<Game>> sol = new ArrayList<List<Game>>();
		for (int round = 0; round < solution.size(); round++) {
			List<Game> sol2 = new ArrayList<Game>();
			for (int city = 0; city < solution.get(0).size(); city++) {
				Game game = solution.get(round).get(city);
				sol2.add(game);
			}
			sol.add(sol2);
		}
		return sol;
	}
	//
	public ArrayList<Integer> getNeigh(int round){
		//round1
		ArrayList<Integer> neigh = new ArrayList<Integer>();
		for(int otherRound = 0; otherRound < solution.size(); otherRound++){
			if(otherRound!=round){
				int costsRound = getSingeleCumulativeCost(round, otherRound);
				neigh.add(otherRound, costsRound);
			} else{
				neigh.add(otherRound, Integer.MAX_VALUE);
			}
		}
		return neigh;
	}
	//
	//
	public List<List<Integer>> getNeigh(){
		List<List<Integer>> neigh = new ArrayList<List<Integer>>();
		for(int round=0; round<neigh.size(); round++){
			neigh.add(getNeigh(round));
		}
		return neigh;
	}
	//
	public boolean checkConstraint(){
		//
		for (int round = 0; round < getRoundsNum(); round++) {
			for (int city = 0; city < getCitiesNum(); city++) {
				//
				Game game = getGameOfCityInRound(city, round);
				//
				if((!game.playAtA) && (city==game.a)){
					boolean canPlayAbroad = canPlayAt(city, round, false, consecHome, conseqRoad);
					if(!canPlayAbroad){
						return true;
					}
				}
				if((game.playAtA) && (city==game.b)){
					boolean canPlayAbroad = canPlayAt(city, round, false, consecHome, conseqRoad);
					if(!canPlayAbroad){
						return true;
					}
				}
				if((game.playAtA) && (city==game.a)){
					boolean canPlayHome = canPlayAt(city, round, true, consecHome, conseqRoad);
					if(!canPlayHome){
						return true;
					}
				}
				if((!game.playAtA) && (city==game.b)){
					boolean canPlayHome = canPlayAt(city, round, true, consecHome, conseqRoad);
					if(!canPlayHome){
						return true;
					}
				}
				//
				if((round!=0)){
					Game otherGame = getGameOfCityInRound(city, round-1);
					if( (game.a==otherGame.a && game.b==otherGame.b) && (game.b==otherGame.a && game.a==otherGame.b)){
						System.out.println("USED check repeat");
						return true;
					}
				}
				
			}
		}
		return false;
	}
	//
	public List<ArrayList<Integer>> getGameList(){
		List<ArrayList<Integer>> gameList = new ArrayList<ArrayList<Integer>>();
		List<Game> games = new ArrayList<Game>();
		for (int round = 0; round < getRoundsNum(); round++) {
			for (int city = 0; city < getCitiesNum(); city++) {
				Game game = getGameOfCityInRound(city, round);
				if(!games.contains(game)){
					games.add(game);
					for (int round2 = round+1; round2 < getRoundsNum(); round2++) {
							List<Game> otherGamesList = solution.get(round2);
							for(int s = 0; s<otherGamesList.size(); s++){
								Game otherGame = otherGamesList.get(s);				
								if(game.comparePlayers(otherGame.a, otherGame.b)){
									games.add(otherGame);
									ArrayList<Integer> gameIdx = new ArrayList<Integer>();
									gameIdx.add(city);
									gameIdx.add(round);
									gameIdx.add(round2);
									gameList.add(gameIdx);
								}
							}
					}
				}
				
				}
		}
		return gameList;
	}
	//
	public List<ArrayList<Integer>> getGameListCosts(){
		List<ArrayList<Integer>> gameList = new ArrayList<ArrayList<Integer>>();
		List<Game> games = new ArrayList<Game>();
		for (int round = 0; round < getRoundsNum(); round++) {
			for (int city = 0; city < getCitiesNum(); city++) {
				Game game = getGameOfCityInRound(city, round);
				if(!games.contains(game)){
					games.add(game);
					for (int round2 = round+1; round2 < getRoundsNum(); round2++) {
							List<Game> otherGamesList = solution.get(round2);
							for(int s = 0; s<otherGamesList.size(); s++){
								Game otherGame = otherGamesList.get(s);				
								if(game.comparePlayers(otherGame.a, otherGame.b)){
									//
									int cost;
									int cost2;
									//
									if(round!=0){
										int lastLoc = getLocationOfGame(city, round-1);
										int gameLocation = getLocationOfGame(city, round);
										cost = problem.getDistance(lastLoc, gameLocation);
										//
										int lastLoc2 = getLocationOfGame(city, round2-1);
										int gameLocation2 = getLocationOfGame(city, round2);
										cost2 = problem.getDistance(lastLoc2, gameLocation2);
									}
									else{
										int gameLocation = getLocationOfGame(city, round);
										cost = problem.getDistance(city, gameLocation);
										//
										int gameLocation2 = getLocationOfGame(city, round2);
										cost2 = problem.getDistance(city, gameLocation2);
									}
									//
									games.add(otherGame);
									//
									ArrayList<Integer> gameIdx = new ArrayList<Integer>();
									gameIdx.add(city);
									gameIdx.add(round);
									gameIdx.add(round2);
									gameIdx.add(cost);
									gameIdx.add(cost2);
									gameList.add(gameIdx);
								}
							}
					}
				}
				
				}
		}
		return gameList;
	}
	//
	public List<ArrayList<Integer>> getGameListWithCnt(){
		List<ArrayList<Integer>> gameList = new ArrayList<ArrayList<Integer>>();
		List<Game> games = new ArrayList<Game>();
		for (int round = 0; round < getRoundsNum(); round++) {
			for (int city = 0; city < getCitiesNum(); city++) {
				Game game = getGameOfCityInRound(city, round);
				///////////////////
				int cntPlay=0;
				if((!game.playAtA) && (city==game.a)){
					cntPlay = canPlayAtCnt(city, round, false, consecHome, conseqRoad);
				}
				if((game.playAtA) && (city==game.b)){
					cntPlay = canPlayAtCnt(city, round, false, consecHome, conseqRoad);
				}
				if((game.playAtA) && (city==game.a)){
					cntPlay = canPlayAtCnt(city, round, true, consecHome, conseqRoad);
				}	
				if((!game.playAtA) && (city==game.b)){
					cntPlay = canPlayAtCnt(city, round, true, consecHome, conseqRoad);
				}
				///////////////////
				//if(!games.contains(game)){
					games.add(game);
					for (int round2 = round+1; round2 < getRoundsNum(); round2++) {
							List<Game> otherGamesList = solution.get(round2);
							for(int s = 0; s<otherGamesList.size(); s++){
								Game otherGame = otherGamesList.get(s);				
								if(game.comparePlayers(otherGame.a, otherGame.b)){
									games.add(otherGame);
									ArrayList<Integer> gameIdx = new ArrayList<Integer>();
									gameIdx.add(city);
									gameIdx.add(round);
									gameIdx.add(round2);
									gameIdx.add(cntPlay);
									gameList.add(gameIdx);
								}
							}
					//}
				}
				
				}
		}
		return gameList;
	}
	//
	public ArrayList<Integer> getRoundListWithCnt(){
		ArrayList<Integer> cntPlayList = new ArrayList<Integer>();
		for (int round = 0; round < getRoundsNum(); round++) {
			int cntPlay=0;
			for (int city = 0; city < getCitiesNum(); city++) {
				Game game = getGameOfCityInRound(city, round);
				if((!game.playAtA) && (city==game.a)){
					cntPlay += canPlayAtCnt(city, round, false, consecHome, conseqRoad);
				}
				if((game.playAtA) && (city==game.b)){
					cntPlay += canPlayAtCnt(city, round, false, consecHome, conseqRoad);
				}
				if((game.playAtA) && (city==game.a)){
					cntPlay += canPlayAtCnt(city, round, true, consecHome, conseqRoad);
				}	
				if((!game.playAtA) && (city==game.b)){
					cntPlay += canPlayAtCnt(city, round, true, consecHome, conseqRoad);
				}
			}
			//System.out.println(cntPlay);
			cntPlayList.add(cntPlay);
		}
		return cntPlayList;
	}
	//
	public List<ArrayList<ArrayList<Integer>>> getGameListPerPlayer(){
		List<ArrayList<ArrayList<Game>>> gameList = new ArrayList<ArrayList<ArrayList<Game>>>();
		List<ArrayList<ArrayList<Integer>>> gameListInteger = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for(int i=0;i<n;i++){
			ArrayList<ArrayList<Game>> gamePlayers = new ArrayList<ArrayList<Game>>();
			gameList.add(gamePlayers);
			ArrayList<ArrayList<Integer>> gamePlayersInteger = new ArrayList<ArrayList<Integer>>();
			gameListInteger.add(gamePlayersInteger);
		}
		for (int round = 0; round < getRoundsNum(); round++) {
			for (int city = 0; city < getCitiesNum(); city++) {
				Game game = getGameOfCityInRound(city, round);
				for (int round2 = 0; round2 < getRoundsNum(); round2++) {
					if(round!=round2){
						List<Game> otherGamesList = solution.get(round2);
						for(int s = 0; s<otherGamesList.size(); s++){
							Game otherGame = otherGamesList.get(s);				
							if(game.comparePlayers(otherGame.a, otherGame.b)){
								ArrayList<Game> gameIdx = new ArrayList<Game>();
								gameIdx.add(game);
								gameIdx.add(otherGame);
								//
								ArrayList<Integer> gameIdxInteger = new ArrayList<Integer>();
								gameIdxInteger.add(city);
								gameIdxInteger.add(round);
								gameIdxInteger.add(round2);
								if(game.playAtA){
									if(!gameList.get(game.a).contains(gameIdx)){
										gameList.get(game.a).add(gameIdx);
										gameListInteger.get(game.a).add(gameIdxInteger);
									}
								}
								else{
									if(!gameList.get(game.b).contains(gameIdx)){
										gameList.get(game.b).add(gameIdx);
										gameListInteger.get(game.b).add(gameIdxInteger);
									}
								}
							}
						}
					}
					}
				}
		}
		return gameListInteger;
	}
}
