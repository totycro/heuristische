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
	 * GAMES
	 */
	/*	 * neighborhood games 	 */
	private void listNeighborhoodGame(List<ArrayList<Integer>> listOfGames, ArrayList<Integer> sortList){
		//
		boolean orgSolutionFalse = sol.checkConstraint();
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
			//
			if(orgSolutionFalse && !badConstraint){
				System.out.println("repair .... NEW SOLUTION (games exchange): "+costs);
				System.out.println(sol);
				orgSolutionFalse = (sol.checkConstraint());
			}
			else if((costsOrg<=costs) || badConstraint){
				sol.setSolution(oldSolution);
			}
			else{
				System.out.println("Improvement: "+game+" to "+otherGame);
			}
		}
	}
	
	private void bestNeighborhoodGame(List<ArrayList<Integer>> listOfGames){
		//
		boolean orgSolutionFalse = sol.checkConstraint();
		//
		ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
		ArrayList<Integer> solPossibleCosts = new ArrayList<Integer>();
		ArrayList<String> solPossibleIdx = new ArrayList<String>();
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
			//
			if(orgSolutionFalse && !badConstraint){
				System.out.println("repair .... NEW SOLUTION (games exchange): "+costs);
				System.out.println(sol);
				orgSolutionFalse = (sol.checkConstraint());
			}
			else if((costsOrg<=costs) || badConstraint){
				sol.setSolution(oldSolution);
			}
			else{
				solPossible.add(sol.deepCopy());
				solPossibleCosts.add(costs);
				solPossibleIdx.add(game+" "+round+" "+round2);
				sol.setSolution(oldSolution);
			}
		}
		if(!solPossible.isEmpty()){
			int minIdx = 0;
			int minIdxCosts = -1;
			for(int s=0; s<solPossibleCosts.size(); s++){
				if(s==0){
					minIdxCosts = solPossibleCosts.get(minIdx);
					sol.setSolution(solPossible.get(minIdx));
				}
				else{
					if(solPossibleCosts.get(s)<minIdxCosts){
						minIdx = s;
						minIdxCosts = solPossibleCosts.get(s);
					}
				}
			}
			sol.setSolution(solPossible.get(minIdx));
			System.out.println("Improvement (game exchange): "+solPossibleIdx.get(minIdx));
			solPossibleCosts.clear();
			solPossible.clear();
		}
		else{
			improvement = false;
		}
		}
	}
	
	
	/*
	 * ROUNDS
	 */
	
	/*	 * neighborhood rounds 	 */
	private void listNeighborhoodRounds(ArrayList<Integer> sortList){
		//
		boolean orgSolutionFalse = sol.checkConstraint();
		//
		for(int idx=0; idx<sol.getRoundsNum(); idx++){
			for(int s=idx+1; s<sol.getRoundsNum(); s++){
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
				if(orgSolutionFalse && !badConstraint){
					System.out.println("repair .... NEW SOLUTION (rounds exchange): "+costs);
					System.out.println(sol);
					orgSolutionFalse = (sol.checkConstraint());
				}
				else if((costsOrg<=costs) || badConstraint){
					sol.setSolution(oldSolution);
				}
				else{
					System.out.println("Improvement: round "+sortIdx+" to "+s);
				}
			}
		}
	}
	
	/*	 * neighborhood rounds 	 */
	private void moveListIdx(int roundCnt){
		//
		boolean orgSolutionFalse = sol.checkConstraint();
		//
		while(roundCnt>0){
		//
		int costsOrg = sol.getCumulativeCost();
		List<List<Game>> oldSolution = sol.deepCopy();
		//
		for(int idx=0; idx<sol.getRoundsNum()-1; idx++){
			sol.getSolution().set(idx+1, oldSolution.get(idx));
		}
		sol.getSolution().set(0, oldSolution.get(sol.getRoundsNum()-1));
		//
		int costs = sol.getCumulativeCost();
		boolean badConstraint = sol.checkConstraint();
		//
		if(orgSolutionFalse && !badConstraint){
			System.out.println("repair .... NEW SOLUTION (move rounds): "+costs);
			System.out.println(sol);
			orgSolutionFalse = (sol.checkConstraint());
		}
		else if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			System.out.println("Improvement: round "+roundCnt+": "+costs);
		}
		//
		roundCnt = roundCnt-1;
		}
	}
	
	private void bestNeighborhoodRounds(){
		//
		boolean orgSolutionFalse = sol.checkConstraint();
		//
		ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
		ArrayList<Integer> solPossibleCosts = new ArrayList<Integer>();
		ArrayList<String> solPossibleIdx = new ArrayList<String>();
		boolean improvement = true;
		//
		while(improvement){
			//
			for(int idx=0; idx<sol.getRoundsNum(); idx++){
				for(int s=idx+1; s<sol.getRoundsNum(); s++){
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
				if(orgSolutionFalse && !badConstraint){
					System.out.println("repair .... NEW SOLUTION (rounds exchange): "+costs);
					System.out.println(sol);
					orgSolutionFalse = (sol.checkConstraint());
				}
				else if((costsOrg<=costs) || badConstraint){
					sol.setSolution(oldSolution);
				}
				else{
					solPossible.add(sol.deepCopy());
					solPossibleCosts.add(costs);
					solPossibleIdx.add(idx+" "+s);
					sol.setSolution(oldSolution);
				}
				}
			}
			if(!solPossible.isEmpty()){
				int minIdx = 0;
				int minIdxCosts = -1;
				for(int s=0; s<solPossibleCosts.size(); s++){
					if(s==0){
						minIdxCosts = solPossibleCosts.get(minIdx);
						sol.setSolution(solPossible.get(minIdx));
					}
					else{
						if(solPossibleCosts.get(s)<minIdxCosts){
							minIdx = s;
							minIdxCosts = solPossibleCosts.get(s);
						}
					}
				}
				sol.setSolution(solPossible.get(minIdx));
				System.out.println("Improvement (rounds exchange): "+solPossibleIdx.get(minIdx));
				solPossibleCosts.clear();
				solPossible.clear();
			}
			else{
				improvement = false;
			}
		}
	}
	
	/*
	 * CALL METHODS
	 */
	/*
	 * neighbor games
	 * 0: random neighborhood
	 * 1: next improvement
	 * 2: best improvement
	 */
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
			for(int i=0; i<sol.getRoundsNum(); i++){
				moveListIdx(i);
				bestNeighborhoodRounds();
			}
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
	
	/*
	 * form random list to sorted list
	 */
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
	
}
