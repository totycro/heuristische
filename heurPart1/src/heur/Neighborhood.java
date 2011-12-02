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
	private boolean orgSolutionFalse;
	private ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
	private ArrayList<String> solPossibleChoice = new ArrayList<String>();
	
	public Neighborhood(Solution solution){
		this.sol = solution;
	}
	
	/*
	 * RANDOM
	 */
	private void randomNeighborhoodRounds(int cntChange, int repeats){
		System.out.println("Round-Change: "+cntChange);
		int cntRound = sol.getRoundsNum();
		//
		for(int j=0; j<repeats; j++){
			for(int idx=0; idx<cntRound; idx++){
				ArrayList<Integer> list = getRandomListWithoutRepeats(cntRound);
				searchRoundImprovement(list);
			}
		}
	}
	
	private void listNeighborhoodGameRandom(List<ArrayList<Integer>> listOfGames, int n, int changeCnt, int sRandom){
		Random rand = new Random();
		for(int idxL=0; idxL<sRandom; idxL++){
			ArrayList<Integer> idxList = new ArrayList<Integer>();
			for(int cnt=0; cnt<changeCnt; cnt++){
				idxList.add(rand.nextInt(n));
			}
			checkSolution(idxList, listOfGames);
		}
	}
	
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
	 * GAMES
	 */
	private String changeGames(int idx, List<ArrayList<Integer>> copyListOfGames){
		ArrayList<Integer> gamesIdx = copyListOfGames.get(idx);
		int city = gamesIdx.get(0);
		int round = gamesIdx.get(1);
		int round2 = gamesIdx.get(2);
		Game game2      = sol.getGameOfCityInRound(city, round);
		Game otherGame2 = sol.getGameOfCityInRound(city, round2);
		int idxGame  = sol.getSolution().get(round).indexOf(game2);
		int idxOtherGame = sol.getSolution().get(round2).indexOf(otherGame2);
		sol.getSolution().get(round).set(idxGame, otherGame2);
		sol.getSolution().get(round2).set(idxOtherGame, game2);
		return " ("+game2+","+otherGame2+") ";
	}
	//
	/*
	private List<Integer> getSortedList(List<ArrayList<Integer>> copyListOfGames, boolean minNotMax){
		//
		int costCnt = copyListOfGames.get(0).size();
		System.out.println(costCnt);
		//
		ArrayList<Integer> listIdx = new ArrayList<Integer>();
		while(!copyListOfGames.isEmpty()){
			int minIdx = -1;
			if(minNotMax){ //0 -> 1 -> 2
				int minCost = Integer.MAX_VALUE;
				for(int i=0; i<copyListOfGames.size(); i++){
					int curCosts;
					if(costCnt==5){
						curCosts = copyListOfGames.get(i).get(3)+copyListOfGames.get(i).get(4);
					}
					else{
						curCosts = copyListOfGames.get(i).get(3);
					}
					if(minCost>=curCosts){
						minIdx = i;
						minCost = curCosts;
					}
				}
			}
			else{ //2 -> 1 -> 0
				int minCost = Integer.MIN_VALUE;
				for(int i=0; i<copyListOfGames.size(); i++){
					int curCosts;
					if(costCnt==5){
						curCosts = copyListOfGames.get(i).get(3)+copyListOfGames.get(i).get(4);
					}
					else{
						curCosts = copyListOfGames.get(i).get(3);
					}
					if(minCost<=curCosts){
						minIdx = i;
						minCost = curCosts;
					}
				}
			}
			listIdx.add(minIdx);
			copyListOfGames.remove(minIdx);
		}
		return listIdx;
	}
	//
	 * */
	private List<Integer> getSortedList(List<ArrayList<Integer>> copyListOfGames, boolean minNotMax){
		//
		ArrayList<Integer> valuesList = new ArrayList<Integer>();
		int costCnt = copyListOfGames.get(0).size();
		//
		for(int i=0; i<copyListOfGames.size(); i++){
			if(costCnt==5){
				int curCosts = copyListOfGames.get(i).get(3)+copyListOfGames.get(i).get(4);
				valuesList.add(curCosts);
			}
			else{
				int curCosts = copyListOfGames.get(i).get(3);
				valuesList.add(curCosts);
			}
		}
		//
		return getSortedListRounds(valuesList, minNotMax);
	}
	//
	private ArrayList<Integer> getSortedListRounds(ArrayList<Integer> roundList, boolean minNotMax){
		//
		ArrayList<Integer> roundListClone = (ArrayList<Integer>) roundList.clone();
		ArrayList<Integer> roundListClone2 = (ArrayList<Integer>) roundList.clone();
		if(minNotMax){
			Collections.sort(roundListClone);
		}
		else{
			Collections.sort(roundListClone);
			Collections.reverse(roundListClone);
		}
		//
		ArrayList<Integer> listIdx = new ArrayList<Integer>();
		for(int j=0; j<roundListClone.size(); j++){
			int idx = roundListClone2.indexOf(roundListClone.get(j));
			listIdx.add(idx);
			roundListClone2.set(idx, Integer.MIN_VALUE);
		}
		return listIdx;
	}
	//
	private void checkSolution(ArrayList<Integer> idxList, List<ArrayList<Integer>> listOfGames){
		String change = "Improve Games: ";
		int costsOrg = sol.getCumulativeCost();
		List<List<Game>> oldSolution = sol.deepCopy();
		
		for(int s=0; s<idxList.size(); s++){
			int a = idxList.get(s);
			change += changeGames(a, listOfGames);
		}
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
			System.out.println(change+": "+costs);
		}
	}
	//
	private void listNeighborhoodRound(int changeCnt, boolean minNotMax) {
		//ArrayList<Integer> roundList = sol.getRoundListWithCnt();
		ArrayList<Integer> roundList = sol.getCumulativeCostOfRound();
		//ArrayList<Integer> roundList = sol.getRoundListWithCnt();
		ArrayList<Integer> list = getSortedListRounds(roundList, minNotMax);
		//
		int sBeginning = 0;
		int sEnding = 1;
		//
		for(int round=0; round<list.size(); round++){
			//
			int sIdx = list.get(round);
			ArrayList<Integer> sortedListToRound1 = sol.getCumulativeCostOfAllRound( sIdx );
			ArrayList<Integer> sortedListToRound = getSortedListRounds(sortedListToRound1, minNotMax);
			//
			//System.out.println(sIdx);
			//System.out.println(sortedListToRound.toString());
			int sList = sortedListToRound.size()-sEnding;
			//
			if(changeCnt==1){
				for(int i=sBeginning; i<sList; i++){
						ArrayList<Integer> indList = new ArrayList<Integer>();
						indList.add(sIdx);
						indList.add(sortedListToRound.get(i));
						searchRoundImprovement(indList);
				}
			}
			else if(changeCnt==2){
				for(int i=sBeginning; i<sList; i++){
					for(int j=i+1; j<sList; j++){
							ArrayList<Integer> indList = new ArrayList<Integer>();
							indList.add((sIdx));
							indList.add(sortedListToRound.get(i));
							indList.add(sortedListToRound.get(j));
							searchRoundImprovement(indList);
				}	}	
			}
			else if(changeCnt==3){
				for(int i=sBeginning; i<sList; i++){
					for(int j=i+1; j<sList; j++){
						for(int k=j+1; k<sList; k++){
								ArrayList<Integer> indList = new ArrayList<Integer>();
								indList.add(sIdx);
								indList.add(sortedListToRound.get(i));
								indList.add(sortedListToRound.get(j));
								indList.add(sortedListToRound.get(k));
								searchRoundImprovement(indList);
				}	}	}	
			}
			else if(changeCnt==4){
				for(int i=sBeginning; i<sList; i++){
					for(int j=i+1; j<sList; j++){
						for(int k=j+1; k<sList; k++){
							for(int s=k+1; s<sList; s++){
								ArrayList<Integer> indList = new ArrayList<Integer>();
								indList.add(sIdx);
								indList.add(sortedListToRound.get(i));
								indList.add(sortedListToRound.get(j));
								indList.add(sortedListToRound.get(k));
								indList.add(sortedListToRound.get(s));
								searchRoundImprovement(indList);
				}}}}
			}
			else if(changeCnt==5){
				for(int i=sBeginning; i<sList; i++){
					for(int j=i+1; j<sList; j++){
						for(int k=j+1; k<sList; k++){
							for(int s=k+1; s<sList; s++){
								for(int t=s+1; t<sList; t++){
								ArrayList<Integer> indList = new ArrayList<Integer>();
								indList.add(sIdx);
								indList.add(sortedListToRound.get(i));
								indList.add(sortedListToRound.get(j));
								indList.add(sortedListToRound.get(k));
								indList.add(sortedListToRound.get(s));
								indList.add(sortedListToRound.get(t));
								searchRoundImprovement(indList);
				}}}}}
			}
			
		}
	}
	/*	 * neighborhood games 	 */
	private void listNeighborhoodGame(List<ArrayList<Integer>> listOfGames, boolean minNotMax, int changeCnt){
		//
		List<ArrayList<Integer>> copyListOfGames = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<listOfGames.size(); i++){
			ArrayList<Integer> list = (ArrayList<Integer>) listOfGames.get(i).clone();
			copyListOfGames.add(list);
		}
		//
		List<Integer> listIdx = getSortedList(copyListOfGames, minNotMax);
		//List<Integer> listIdx = getRandomList(copyListOfGames.size());
		//
		if(changeCnt==1){
			for(int idxL=0; idxL<listIdx.size(); idxL++){
				ArrayList<Integer> idxList = new ArrayList<Integer>();
				idxList.add(listIdx.get(idxL));
				checkSolution(idxList, listOfGames);
		}}
		else if(changeCnt==2){
			for(int idxL=0; idxL<listIdx.size(); idxL++){
				for(int idx=idxL+1; idx<listOfGames.size(); idx++){
						ArrayList<Integer> idxList = new ArrayList<Integer>();
						idxList.add(listIdx.get(idxL));
						idxList.add(listIdx.get(idx));
						checkSolution(idxList, listOfGames);
			}}
		}
		else if(changeCnt==3){
			for(int idxL=0; idxL<listIdx.size(); idxL++){
				for(int idx=idxL+1; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
							ArrayList<Integer> idxList = new ArrayList<Integer>();
							idxList.add(listIdx.get(idxL));
							idxList.add(listIdx.get(idx));
							idxList.add(listIdx.get(idx1));
							checkSolution(idxList, listOfGames);
			}}}
		}
		else if(changeCnt==4){
			for(int idxL=0; idxL<listIdx.size(); idxL++){
				for(int idx=idxL+1; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
									ArrayList<Integer> idxList = new ArrayList<Integer>();
									idxList.add(listIdx.get(idxL));
									idxList.add(listIdx.get(idx));
									idxList.add(listIdx.get(idx1));
									idxList.add(listIdx.get(idx2));
									checkSolution(idxList, listOfGames);
			}}}}
		}
		else if(changeCnt==5){
				for(int idxL=0; idxL<listIdx.size(); idxL++){
					for(int idx=idxL+1; idx<listOfGames.size(); idx++){
						for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
							for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
								for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
										ArrayList<Integer> idxList = new ArrayList<Integer>();
										idxList.add(listIdx.get(idxL));
										idxList.add(listIdx.get(idx));
										idxList.add(listIdx.get(idx1));
										idxList.add(listIdx.get(idx2));
										idxList.add(listIdx.get(idx3));
										checkSolution(idxList, listOfGames);
				}}}}}
		}
		else if(changeCnt==6){
			for(int idxL=0; idxL<listIdx.size(); idxL++){
				for(int idx=idxL+1; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
								for(int idx4=idx3+1; idx4<listOfGames.size(); idx4++){
									ArrayList<Integer> idxList = new ArrayList<Integer>();
									idxList.add(listIdx.get(idxL));
									idxList.add(listIdx.get(idx));
									idxList.add(listIdx.get(idx1));
									idxList.add(listIdx.get(idx2));
									idxList.add(listIdx.get(idx3));
									idxList.add(listIdx.get(idx4));
									checkSolution(idxList, listOfGames);
			}}}}}}
	}
	}
	
	/*
	 * BEST
	 */
	private void searchPossibleGamesImprovements(List<ArrayList<Integer>> list, ArrayList<Integer> idxList){
		//
		int costsOrg = sol.getCumulativeCost();
		List<List<Game>> oldSolution = sol.deepCopy();
		String change = "";
		//
		for(int ind=0; ind<idxList.size(); ind++){
				ArrayList<Integer> gamesIdx = list.get(idxList.get(ind));
				int city = gamesIdx.get(0);
				int round = gamesIdx.get(1);
				int round2 = gamesIdx.get(2);
				Game game      = sol.getGameOfCityInRound(city, round);
				Game otherGame = sol.getGameOfCityInRound(city, round2);
				int idxGame  = sol.getSolution().get(round).indexOf(game);
				int idxOtherGame = sol.getSolution().get(round2).indexOf(otherGame);
				sol.getSolution().get(round).set(idxGame, otherGame);
				sol.getSolution().get(round2).set(idxOtherGame, game);
				change = change+" ("+game+" to "+otherGame+") ";
		}
		//
		int costs = sol.getCumulativeCost();
		boolean badConstraint = sol.checkConstraint();
		//
		change = change+": "+costs;
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
			solPossibleChoice.add(change);
			sol.setSolution(oldSolution);
		}
	}
	
	private boolean setBestImprovement(){
		if(!solPossible.isEmpty()){
			int minIdx = 0;
			int minIdxCosts = -1;
			for(int s=0; s<solPossible.size(); s++){
				sol.setSolution(solPossible.get(s));
				int currentCosts = sol.getCumulativeCost();
				if(s==0){
					minIdxCosts = currentCosts;
				}
				else{
					if(currentCosts<minIdxCosts){
						minIdx = s;
						minIdxCosts = currentCosts;
					}
				}
			}
			sol.setSolution(solPossible.get(minIdx));
			System.out.println(solPossibleChoice.get(minIdx));
			System.out.println(solPossible.size());
			solPossibleChoice.clear();
			solPossible.clear();
		}
		else{
			return false;
		}
		return true;
	}
	
	private void bestNeighborhoodGame(List<ArrayList<Integer>> listOfGames, int cntChange){
		
		System.out.println("Change "+cntChange+" games");
		
		boolean improvement = true;
		while(improvement){
			
			if(cntChange==1){
				for(int idx=0; idx<listOfGames.size(); idx++){
					ArrayList<Integer> idxList = new ArrayList<Integer>();
					idxList.add(idx);
					searchPossibleGamesImprovements(listOfGames, idxList);
				}
			}
			else if(cntChange==2){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						ArrayList<Integer> idxList = new ArrayList<Integer>();
						idxList.add(idx);
						idxList.add(idx1);
						searchPossibleGamesImprovements(listOfGames, idxList);
				}}
			}
			else if(cntChange==3){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							ArrayList<Integer> idxList = new ArrayList<Integer>();
							idxList.add(idx);
							idxList.add(idx1);
							idxList.add(idx2);
							searchPossibleGamesImprovements(listOfGames, idxList);
				}}}
			}
			else if(cntChange==4){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
								for(int idx4=idx3+1; idx4<listOfGames.size(); idx4++){				
									ArrayList<Integer> idxList = new ArrayList<Integer>();
									idxList.add(idx);
									idxList.add(idx1);
									idxList.add(idx2);
									idxList.add(idx3);
									idxList.add(idx4);
									searchPossibleGamesImprovements(listOfGames, idxList);
				}}}}}
			}
			else if(cntChange==5){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
								for(int idx4=idx3+1; idx4<listOfGames.size(); idx4++){				
									for(int idx5=idx4+1; idx5<listOfGames.size(); idx5++){
										ArrayList<Integer> idxList = new ArrayList<Integer>();
										idxList.add(idx);
										idxList.add(idx1);
										idxList.add(idx2);
										idxList.add(idx3);
										idxList.add(idx4);
										idxList.add(idx5);
										searchPossibleGamesImprovements(listOfGames, idxList);
				}}}}}}
			}
			else if(cntChange==6){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
								for(int idx4=idx3+1; idx4<listOfGames.size(); idx4++){				
									for(int idx5=idx4+1; idx5<listOfGames.size(); idx5++){
										for(int idx6=idx5+1; idx6<listOfGames.size(); idx6++){
											ArrayList<Integer> idxList = new ArrayList<Integer>();
											idxList.add(idx);
											idxList.add(idx1);
											idxList.add(idx2);
											idxList.add(idx3);
											idxList.add(idx4);
											idxList.add(idx5);
											idxList.add(idx6);
											searchPossibleGamesImprovements(listOfGames, idxList);
				}}}}}}}
			}
			else if(cntChange==7){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
								for(int idx4=idx3+1; idx4<listOfGames.size(); idx4++){				
									for(int idx5=idx4+1; idx5<listOfGames.size(); idx5++){
										for(int idx6=idx5+1; idx6<listOfGames.size(); idx6++){
											for(int idx7=idx6+1; idx7<listOfGames.size(); idx7++){
											ArrayList<Integer> idxList = new ArrayList<Integer>();
											idxList.add(idx);
											idxList.add(idx1);
											idxList.add(idx2);
											idxList.add(idx3);
											idxList.add(idx4);
											idxList.add(idx5);
											idxList.add(idx6);
											idxList.add(idx7);
											searchPossibleGamesImprovements(listOfGames, idxList);
				}}}}}}}}
			}
			else if(cntChange==8){
				for(int idx=0; idx<listOfGames.size(); idx++){
					for(int idx1=idx+1; idx1<listOfGames.size(); idx1++){
						for(int idx2=idx1+1; idx2<listOfGames.size(); idx2++){
							for(int idx3=idx2+1; idx3<listOfGames.size(); idx3++){
								for(int idx4=idx3+1; idx4<listOfGames.size(); idx4++){				
									for(int idx5=idx4+1; idx5<listOfGames.size(); idx5++){
										for(int idx6=idx5+1; idx6<listOfGames.size(); idx6++){
											for(int idx7=idx6+1; idx7<listOfGames.size(); idx7++){
												for(int idx8=idx7+1; idx8<listOfGames.size(); idx8++){
													ArrayList<Integer> idxList = new ArrayList<Integer>();
													idxList.add(idx);
													idxList.add(idx1);
													idxList.add(idx2);
													idxList.add(idx3);
													idxList.add(idx4);
													idxList.add(idx5);
													idxList.add(idx6);
													idxList.add(idx7);
													idxList.add(idx8);
													searchPossibleGamesImprovements(listOfGames, idxList);
				}}}}}}}}}
			}
			improvement = setBestImprovement();
		}
	}
	
	/*
	 * ROUNDS
	 */
	
	/*	 * neighborhood rounds 	 */
	private void listNeighborhoodRounds(ArrayList<Integer> sortList){
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
					System.out.println("Improvement: round "+sortIdx+" to "+s+" "+costs);
				}
			}
		}
	}
	
	/*	 * neighborhood rounds 	 */
	private void moveListIdx(int roundCnt){
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
	
	private void searchPossibleRoundImprovements(ArrayList<Integer> list){
		//
		int costsOrg = sol.getCumulativeCost();
		List<List<Game>> oldSolution = sol.deepCopy();
		List<List<Game>> listRounds = new ArrayList<List<Game>>();
		List<Integer> idxRounds = new ArrayList<Integer>();
		String change = "Rounds: ";
		//
		for(int ind=0; ind<list.size(); ind++){
			int idx = list.get(ind);
			List<Game> round = sol.getRound(idx);
			listRounds.add(round);
			change = change+" "+idx+" ";
			if(ind==(list.size()-1)){
				idxRounds.add(list.get(0));
			}
			else{
				idxRounds.add(list.get(ind+1));
			}
		}
		//
		for(int ind=0; ind<list.size(); ind++){
			sol.getSolution().set(idxRounds.get(ind), listRounds.get(ind));
		}
		//
		int costs = sol.getCumulativeCost();
		boolean badConstraint = sol.checkConstraint();
		//
		change = change+": "+costs;
		//
		/*
		if(!badConstraint){
			System.out.println(costs);
		}
		*/
		if(orgSolutionFalse && !badConstraint){
			System.out.println("repair .... NEW SOLUTION (round exchange): "+costs);
			System.out.println(sol);
			orgSolutionFalse = (sol.checkConstraint());
		}
		else if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			System.out.println(costs);
			solPossible.add(sol.deepCopy());
			solPossibleChoice.add(change);
			sol.setSolution(oldSolution);
		}
	}
	
	private void bestNeighborhoodRounds(int cntChange){
		
		System.out.println("Round-Change: "+cntChange);
		boolean improvement = true;
		int cntRound = sol.getRoundsNum();
		
		while(improvement){
			//
			if(cntChange==1){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						ArrayList<Integer> list = new ArrayList<Integer>();
						list.add(idx);
						list.add(idx1);
						searchPossibleRoundImprovements(list);
				}}
			}
			else if(cntChange==2){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							ArrayList<Integer> list = new ArrayList<Integer>();
							list.add(idx);
							list.add(idx1);
							list.add(idx2);
							searchPossibleRoundImprovements(list);
				}	}	}
			}
			else if(cntChange==3){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								ArrayList<Integer> list = new ArrayList<Integer>();
								list.add(idx);
								list.add(idx1);
								list.add(idx2);
								list.add(idx3);
								searchPossibleRoundImprovements(list);
				}	}	}	}
			}
			else if(cntChange==4){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									ArrayList<Integer> list = new ArrayList<Integer>();
									list.add(idx);
									list.add(idx1);
									list.add(idx2);
									list.add(idx3);
									list.add(idx4);
									searchPossibleRoundImprovements(list);
				}	}	}	}	}
			}
			else if(cntChange==5){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										ArrayList<Integer> list = new ArrayList<Integer>();
										list.add(idx);
										list.add(idx1);
										list.add(idx2);
										list.add(idx3);
										list.add(idx4);
										list.add(idx5);
										searchPossibleRoundImprovements(list);
				}	}	}	}	}	}
			}
			else if(cntChange==6){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											ArrayList<Integer> list = new ArrayList<Integer>();
											list.add(idx);
											list.add(idx1);
											list.add(idx2);
											list.add(idx3);
											list.add(idx4);
											list.add(idx5);
											list.add(idx6);
											searchPossibleRoundImprovements(list);
			}}}}}}}}
			else if(cntChange==7){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												ArrayList<Integer> list = new ArrayList<Integer>();
												list.add(idx);
												list.add(idx1);
												list.add(idx2);
												list.add(idx3);
												list.add(idx4);
												list.add(idx5);
												list.add(idx6);
												list.add(idx7);
												searchPossibleRoundImprovements(list);
			}}}}}}}}}
			else if(cntChange==8){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													ArrayList<Integer> list = new ArrayList<Integer>();
													list.add(idx);
													list.add(idx1);
													list.add(idx2);
													list.add(idx3);
													list.add(idx4);
													list.add(idx5);
													list.add(idx6);
													list.add(idx7);
													list.add(idx8);
													searchPossibleRoundImprovements(list);
			}}}}}}}}}}
			else if(cntChange==9){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														ArrayList<Integer> list = new ArrayList<Integer>();
														list.add(idx);
														list.add(idx1);
														list.add(idx2);
														list.add(idx3);
														list.add(idx4);
														list.add(idx5);
														list.add(idx6);
														list.add(idx7);
														list.add(idx8);
														list.add(idx9);
														searchPossibleRoundImprovements(list);
			}}}}}}}}}}}
			else if(cntChange==10){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
																ArrayList<Integer> list = new ArrayList<Integer>();
																list.add(idx);
																list.add(idx1);
																list.add(idx2);
																list.add(idx3);
																list.add(idx4);
																list.add(idx5);
																list.add(idx6);
																list.add(idx7);
																list.add(idx8);
																list.add(idx9);
																list.add(idx10);
																searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}
			else if(cntChange==11){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}
			else if(cntChange==12){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																for(int idx12=idx11+1; idx12<cntRound; idx12++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		list.add(idx12);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}}
			else if(cntChange==13){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																for(int idx12=idx11+1; idx12<cntRound; idx12++){
																	for(int idx13=idx12+1; idx13<cntRound; idx13++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		list.add(idx12);
																		list.add(idx13);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}}}
			else if(cntChange==14){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																for(int idx12=idx11+1; idx12<cntRound; idx12++){
																	for(int idx13=idx12+1; idx13<cntRound; idx13++){
																		for(int idx14=idx13+1; idx14<cntRound; idx14++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		list.add(idx12);
																		list.add(idx13);
																		list.add(idx14);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}}}}
			else if(cntChange==15){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																for(int idx12=idx11+1; idx12<cntRound; idx12++){
																	for(int idx13=idx12+1; idx13<cntRound; idx13++){
																		for(int idx14=idx13+1; idx14<cntRound; idx14++){
																			for(int idx15=idx14+1; idx15<cntRound; idx15++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		list.add(idx12);
																		list.add(idx13);
																		list.add(idx14);
																		list.add(idx15);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}}}}}
			else if(cntChange==16){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																for(int idx12=idx11+1; idx12<cntRound; idx12++){
																	for(int idx13=idx12+1; idx13<cntRound; idx13++){
																		for(int idx14=idx13+1; idx14<cntRound; idx14++){
																			for(int idx15=idx14+1; idx15<cntRound; idx15++){
																				for(int idx16=idx15+1; idx16<cntRound; idx16++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		list.add(idx12);
																		list.add(idx13);
																		list.add(idx14);
																		list.add(idx15);
																		list.add(idx16);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}}}}}}
			else if(cntChange==17){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								for(int idx4=idx3+1; idx4<cntRound; idx4++){
									for(int idx5=idx4+1; idx5<cntRound; idx5++){
										for(int idx6=idx5+1; idx6<cntRound; idx6++){
											for(int idx7=idx6+1; idx7<cntRound; idx7++){
												for(int idx8=idx7+1; idx8<cntRound; idx8++){
													for(int idx9=idx8+1; idx9<cntRound; idx9++){
														for(int idx10=idx9+1; idx10<cntRound; idx10++){
															for(int idx11=idx10+1; idx11<cntRound; idx11++){
																for(int idx12=idx11+1; idx12<cntRound; idx12++){
																	for(int idx13=idx12+1; idx13<cntRound; idx13++){
																		for(int idx14=idx13+1; idx14<cntRound; idx14++){
																			for(int idx15=idx14+1; idx15<cntRound; idx15++){
																				for(int idx16=idx15+1; idx16<cntRound; idx16++){
																					for(int idx17=idx16+1; idx17<cntRound; idx17++){
																		ArrayList<Integer> list = new ArrayList<Integer>();
																		list.add(idx);
																		list.add(idx1);
																		list.add(idx2);
																		list.add(idx3);
																		list.add(idx4);
																		list.add(idx5);
																		list.add(idx6);
																		list.add(idx7);
																		list.add(idx8);
																		list.add(idx9);
																		list.add(idx10);
																		list.add(idx11);
																		list.add(idx12);
																		list.add(idx13);
																		list.add(idx14);
																		list.add(idx15);
																		list.add(idx16);
																		list.add(idx17);
																		searchPossibleRoundImprovements(list);
			}}}}}}}}}}}}}}}}}}}
			improvement = setBestImprovement();
		}
	}

	private void searchRoundImprovement(ArrayList<Integer> list){
		//
		int costsOrg = sol.getCumulativeCost();
		List<List<Game>> oldSolution = sol.deepCopy();
		List<List<Game>> listRounds = new ArrayList<List<Game>>();
		List<Integer> idxRounds = new ArrayList<Integer>();
		String change = "Rounds: ";
		//
		for(int ind=0; ind<list.size(); ind++){
			int idx = list.get(ind);
			List<Game> round = sol.getRound(idx);
			listRounds.add(round);
			change = change+" "+idx+" ";
			if(ind==(list.size()-1)){
				idxRounds.add(list.get(0));
			}
			else{
				idxRounds.add(list.get(ind+1));
			}
		}
		//
		for(int ind=0; ind<list.size(); ind++){
			sol.getSolution().set(idxRounds.get(ind), listRounds.get(ind));
		}
		//
		int costs = sol.getCumulativeCost();
		boolean badConstraint = sol.checkConstraint();
		//
		if(orgSolutionFalse && !badConstraint){
			System.out.println("repair .... NEW SOLUTION (round exchange): "+costs);
			System.out.println(sol);
			orgSolutionFalse = (sol.checkConstraint());
		}
		else if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			System.out.println("Improvement "+change+": "+costs);
			//System.out.println(sol);
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
		orgSolutionFalse = sol.checkConstraint();
		//
		if(neighChoice==0){ //random neighbor
			for(int i=0; i<sol.getRoundsNum(); i++){
				randomNeighborhoodRounds(i,1000);
			}
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			//ArrayList<Integer> sortList  = getList(sol.getRoundsNum());
			/*
			ArrayList<Integer> sortList = new ArrayList<Integer>();
			for(int i=0; i<sol.getRoundsNum(); i++){
				sortList.add(neighborhoodList(i,0));
			}
			listNeighborhoodRounds(sortList);
			*/
			//ArrayList<Integer> roundList = sol.getCumulativeCostOfRound();
			//ArrayList<Integer> roundList = sol.getRoundListWithCnt();
			//ArrayList<Integer> al = getSortedListRounds(roundList, true);
			//ArrayList<Integer> getRoundListWithCnt()
			
			boolean minNotMax = false;
			int changeCnt = 4;
			for(int i=0; i<sol.getRoundsNum(); i++){
				listNeighborhoodRound(i,minNotMax);
			}
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			//for(int i=1; i<sol.getRoundsNum(); i++){
			for(int i=0; i<sol.getRoundsNum(); i++){
				bestNeighborhoodRounds(i);
			}
			/*
			for(int i=0; i<sol.getRoundsNum(); i++){
				moveListIdx(i);
			}
			*/
			for(int i=0; i<sol.getRoundsNum(); i++){
				//for(int i=sol.getRoundsNum(); i>0; i--){
					moveListIdx(i);
					bestNeighborhoodRounds(1);
					List<ArrayList<Integer>> listOfGames = sol.getGameList();
					bestNeighborhoodGame(listOfGames,5);
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
		orgSolutionFalse = sol.checkConstraint();
		//
		List<ArrayList<Integer>> listOfGames = sol.getGameList();
		if(neighChoice==0){ //random neighbor
			//ArrayList<Integer> sortList = getRandomListWithoutRepeats(listOfGames.size());
			//ArrayList<Integer> sortList = getRandomList(listOfGames.size());
			//listNeighborhoodGame(listOfGames, sortList);
			//listNeighborhoodGameRandom(List<ArrayList<Integer>> listOfGames, int n, int changeCnt, int sRandom)
			for(int i=0; i<5; i++){
				listNeighborhoodGameRandom(listOfGames, listOfGames.size(), i, 15000);
			}
			
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			//ArrayList<Integer> sortList  = getList(listOfGamesCnt.size());
			//List<ArrayList<Integer>> listOfGamesCnt = sol.getGameListWithCnt();
			//List<ArrayList<Integer>> listOfGamesCnt = sol.getGameListCosts(false);
			//listNeighborhoodGame(listOfGamesCnt,true);
			List<ArrayList<Integer>> listOfGamesCnt = sol.getGameListCosts();
			for(int i=0; i<4; i++){
				listNeighborhoodGame(listOfGamesCnt,true,i);
			}
			
			//
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			//bestNeighborhoodGame(listOfGames,1);
			//bestNeighborhoodGame(listOfGames,5);
			for(int i=0; i<6; i++){
				bestNeighborhoodGame(listOfGames,i);
			}
		}
		//
		System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost());
		//
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
