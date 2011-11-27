package heur;

import heur.Solution.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Neighborhood {

	private Solution sol;
	
	public Neighborhood(Solution solution){
		this.sol = solution;
	}
	
	/*
	 * neighborhood games 
	 */
	private void listNeighborhoodGame(List<ArrayList<Integer>> listOfGames, ArrayList<Integer> sortList){
		//
		for(int idx=0; idx<sortList.size(); idx++){
			//
			int costsOrg = sol.getCumulativeCost();
			List<List<Game>> oldSolution = sol.deepCopy();
			//
			ArrayList<Integer> gamesIdx = listOfGames.get(sortList.get(idx));
			int city = gamesIdx.get(0);
			int round = gamesIdx.get(1);
			int round2 = gamesIdx.get(2);
			//
			Game game      = sol.getGameOfCityInRound(city, round);
			Game otherGame = sol.getGameOfCityInRound(city, round2);
			//
			int idxGame  = sol.getSolution().get(round).indexOf(game);
			int idxOtherGame = sol.getSolution().get(round2).indexOf(otherGame);
			//
			sol.getSolution().get(round).set(idxGame, otherGame);
			sol.getSolution().get(round2).set(idxOtherGame, game);
			//
			int costs = sol.getCumulativeCost();
			boolean badConstraint = sol.checkConstraint();
			if((costsOrg<=costs) || badConstraint){
				sol.setSolution(oldSolution);
			}
			else{
				System.out.println("Improvement: "+game+" to "+otherGame);
			}
		}
	}
	
	/*
	 * neighborhood rounds 
	 */
	private void listNeighborhoodRounds(ArrayList<Integer> sortList){
		//
		for(int idx=0; idx<sol.getRoundsNum(); idx++){
			for(int s=0; s<sol.getRoundsNum(); s++){
				//
				int costsOrg = sol.getCumulativeCost();
				List<List<Game>> oldSolution = sol.deepCopy();
				//
				int sortIdx = sortList.get(idx);
				List<Game> curRound   = sol.getRound(sortIdx);
				List<Game> otherRound = sol.getRound(s);
				//
				sol.getSolution().set(sortIdx, otherRound);
				sol.getSolution().set(s,  curRound);
				//
				int costs = sol.getCumulativeCost();
				boolean badConstraint = sol.checkConstraint();
				//
				if((costsOrg<=costs) || badConstraint){
					sol.setSolution(oldSolution);
				}
				else{
					System.out.println("Improvement: round "+sortIdx+" to "+s);
				}
			}
		}
	}
	
	private void bestNeighborhoodGame(List<ArrayList<Integer>> listOfGames){
		//
		ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
		List<List<Game>> bestSol = null;
		int bestSolCost = -1;
		boolean improvement = true;
		//
		while(improvement){
			//
		for(int idx=0; idx<listOfGames.size(); idx++){
			//
			int costsOrg = sol.getCumulativeCost();
			List<List<Game>> oldSolution = sol.deepCopy();
			//
			ArrayList<Integer> gamesIdx = listOfGames.get(idx);
			int city = gamesIdx.get(0);
			int round = gamesIdx.get(1);
			int round2 = gamesIdx.get(2);
			//
			Game game      = sol.getGameOfCityInRound(city, round);
			Game otherGame = sol.getGameOfCityInRound(city, round2);
			//
			int idxGame  = sol.getSolution().get(round).indexOf(game);
			int idxOtherGame = sol.getSolution().get(round2).indexOf(otherGame);
			//
			sol.getSolution().get(round).set(idxGame, otherGame);
			sol.getSolution().get(round2).set(idxOtherGame, game);
			//
			int costs = sol.getCumulativeCost();
			boolean badConstraint = sol.checkConstraint();
			if((costsOrg<=costs) || badConstraint){
				sol.setSolution(oldSolution);
			}
			else{
				solPossible.add(sol.deepCopy());
				System.out.println("possible change: "+game+" "+otherGame+": "+costs);
				//
				if( bestSol==null || costs<bestSolCost ){
					bestSol = sol.deepCopy();
					bestSolCost = costs;
					System.out.println("new solution ");
				}
				//
				sol.setSolution(oldSolution);
			}
		}
		if(!solPossible.isEmpty()){
			sol.setSolution(solPossible.get(0));
			solPossible.remove(0);
		}
		else{
			improvement = false;
		}
		}
		if(bestSol!=null){
			sol.setSolution(bestSol);
		}
	}
	
	private void bestNeighborhoodRounds(){
		//
		ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
		List<List<Game>> bestSol = null;
		int bestSolCost = -1;
		boolean improvement = true;
		//
		while(improvement){
			//
		for(int idx=0; idx<sol.getRoundsNum(); idx++){
			for(int s=idx; s<sol.getRoundsNum(); s++){
				//
				int costsOrg = sol.getCumulativeCost();
				List<List<Game>> oldSolution = sol.deepCopy();
				//
				List<Game> curRound   = sol.getRound(idx);
				List<Game> otherRound = sol.getRound(s);
				//
				sol.getSolution().set(idx, otherRound);
				sol.getSolution().set(s,  curRound);
				//
				int costs = sol.getCumulativeCost();
				boolean badConstraint = sol.checkConstraint();
				//
				if((costsOrg<=costs) || badConstraint){
					sol.setSolution(oldSolution);
				}
				else{
					solPossible.add(sol.deepCopy());
					System.out.println("possible change: "+idx+" "+s+": "+costs);
					//
					if( bestSol==null || costs<bestSolCost ){
						bestSol = sol.deepCopy();
						bestSolCost = costs;
						System.out.println("new solution ");
					}
					//
					sol.setSolution(oldSolution);
				}
			}
		}
		if(!solPossible.isEmpty()){
			sol.setSolution(solPossible.get(0));
			solPossible.remove(0);
		}
		else{
			improvement = false;
		}
		}
		if(bestSol!=null){
			sol.setSolution(bestSol);
		}
	}
	
	//still in work
	private void bestNeighborhoodRoundsBigger(){
		//
		LinkedList<List<List<Game>>> solPossible = new LinkedList<List<List<Game>>>();
		//
		List<List<Game>> bestSol = null;
		int bestSolCost = -1;
		boolean improvement = true;
		int improvementCnt = 0;
		//
		//Permutation p = new Permutation(3);
		//
		while(improvement){
			//
		improvementCnt = 0;
		for(int idx=0; idx<sol.getRoundsNum(); idx++){
			for(int s=idx; s<sol.getRoundsNum(); s++){
				for(int t=s; t<sol.getRoundsNum(); t++){
				//
				int costsOrg = sol.getCumulativeCost();
				List<List<Game>> oldSolution = sol.deepCopy();
				//
				List<Game> curRound   = sol.getRound(idx);
				List<Game> otherRound = sol.getRound(s);
				List<Game> thirdRound = sol.getRound(t);
				//
				sol.getSolution().set(idx, thirdRound);
				sol.getSolution().set(  s, curRound);
				sol.getSolution().set(  t, otherRound);
				//
				int costs = sol.getCumulativeCost();
				boolean badConstraint = sol.checkConstraint();
				//
				if((costsOrg<=costs) || badConstraint){
					sol.setSolution(oldSolution);
				}
				else{
					//System.out.println("possible change: "+idx+" "+s+" "+t+": "+costs);
					solPossible.add(sol.deepCopy());
					//
					if( bestSol==null || costs<bestSolCost ){
						bestSol = sol.deepCopy();
						bestSolCost = costs;
						System.out.println("new solution "+costs);
						improvementCnt = improvementCnt+1;
					}
					//
					sol.setSolution(oldSolution);
				}
				}
			}
		}
		if(!solPossible.isEmpty()){
			//sol.setSolution(solPossible.get(0));
			//solPossible.remove(0);
			sol.setSolution(solPossible.poll());
		}
		else{
			improvement = false;
		}
		}
		if(bestSol!=null){
			sol.setSolution(bestSol);
		}
	}
	
	public void neighborhoodRounds(int neighChoice){	
		//
		if(neighChoice==0){ //random neighbor
			ArrayList<Integer> sortList  = getRandomList(sol.getRoundsNum());
			listNeighborhoodRounds(sortList);
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			//ArrayList<Integer> sortList  = getList(sol.getRoundsNum());
			ArrayList<Integer> sortList = new ArrayList<Integer>();
			for(int i=0; i<sol.getRoundsNum(); i++){
				sortList.add(neighborhoodList(i,0));
			}
			listNeighborhoodRounds(sortList);
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			bestNeighborhoodRounds();
		}
		else if(neighChoice==3){
			//not working yet
			//bestNeighborhoodRoundsBigger();
		}
		//
		System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost());
		//
	}
	
	/*
	 * neighbor games
	 * 0: random neighborhood
	 * 1: next improvement
	 * 2: best improvement
	 */
	public void neighborhoodGames(int neighChoice){
		//
		List<ArrayList<Integer>> listOfGames = sol.getGameList();
		if(neighChoice==0){ //random neighbor
			//ArrayList<Integer> sortList = getRandomListWithoutRepeats(listOfGames.size());
			ArrayList<Integer> sortList = getRandomList(listOfGames.size());
			listNeighborhoodGame(listOfGames, sortList);
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			ArrayList<Integer> sortList  = getList(listOfGames.size());
			listNeighborhoodGame(listOfGames, sortList);
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			bestNeighborhoodGame(listOfGames);
		}
		//
		System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost());
		//
	}
	
	//get random list with all numbers of n
	private ArrayList<Integer> getRandomListWithoutRepeats(int n){
		ArrayList<Integer> randomList = new ArrayList<Integer>();
		Random rand = new Random();
		for(int i=0; i<n; i++){
			randomList.add(i);
		}
		for(int j=0; j<n; j++){
			int tempIdx = rand.nextInt(n);
			//
			int temp = randomList.get(tempIdx);
			int temp2 = randomList.get(j);
			//
			randomList.set(tempIdx,temp2);
			randomList.set(      j,temp);
		}
		return randomList;
	}
	
	//get list from 0 to n
	private ArrayList<Integer> getList(int n){
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; i<n; i++){
			list.add(i);
		}
		return list;
	}
	
	//get random list for random neighborhood
	private ArrayList<Integer> getRandomList(int n){
		ArrayList<Integer> randomList = new ArrayList<Integer>();
		Random rand = new Random();
		for(int i=0; i<n; i++){
			randomList.add(rand.nextInt(n));
		}
		return randomList;
	}
	
	/*
	 * neighborhood-list
	 * calculate distance between rounds
	 */
	private int neighborhoodList(int round, int idx) {
		ArrayList<Integer> neighL = sol.getNeigh(round);
		ArrayList<Integer> sortList = (ArrayList<Integer>) neighL.clone();
		Collections.sort(sortList);
		int sortIdx = (neighL.indexOf(sortList.get(idx)));
		return sortIdx;
	}
	
	//currently not used
	private void changeToMax(ArrayList<ArrayList<Integer>> changePos, int struc){
		int minIdx = -1;
		int minCosts = -1;
		for(int ind = 0; ind<changePos.size(); ind++){
			int cost = changePos.get(ind).get(0);
			if(minIdx<0 || minCosts>cost){
				minIdx = ind;
				minCosts = cost;
			}
		}
		ArrayList<Integer> improv = changePos.get(minIdx);
		//
		if(struc==0){
			int roundA = improv.get(1);
			int roundB = improv.get(2);
			//
			List<Game> g1 = (sol.getSolution().get(roundA));
			List<Game> g2 = (sol.getSolution().get(roundB));
			//
			sol.getSolution().set(roundA, g1);
			sol.getSolution().set(roundB, g2);
		}
		else{
			//		
			int roundA = improv.get(1);
			int idxA = improv.get(2);
			int roundB = improv.get(3);
			int idxB = improv.get(4);
			//
			Game g1 = (sol.getSolution().get(roundA).get(idxA));
			Game g2 = (sol.getSolution().get(roundB).get(idxB));
			//
			sol.getSolution().get(roundA).set(idxA, g2);
			sol.getSolution().get(roundB).set(idxB, g1);
			//
			System.out.println("change: "+g1+" "+g2);
			//
		}
		//
		int costs = sol.getCumulativeCost();
		//
		System.out.println("Improvement: "+costs);
		//
		changePos.clear();
	}
	
}
