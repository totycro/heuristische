package heur;

import heur.Solution.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Neighborhood {

	private Solution sol;
	private boolean orgSolutionFalse;
	private ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
	private ArrayList<String> solPossibleChoice = new ArrayList<String>();
	private boolean improvement;
	private int iterationStep;
	private boolean minNotMax = false;
	
	public Neighborhood(Solution solution){
		this.sol = solution;
	}
	
	/*
	 * RANDOM
	 */
	private void randomNeighborhoodRounds(int cntChange, int times){
		String text = (cntChange+" round exchanges, "+times+" tries");
		System.out.println(text);
		if(Util.protocolBoolean){
			Util.protocol += text+"\n";
		}
		//
		int maxValue = sol.getRoundsNum();
		for(int j=0; j<times; j++){
			ArrayList<Integer> list = getRandomListWithoutRepeats(maxValue, cntChange);
			searchRoundImprovement(list);
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
			idxList.clear();
		}
	}
	
	private ArrayList<Integer> getRandomListWithoutRepeats(int valueMax, int sMax){
		ArrayList<Integer> randomList = new ArrayList<Integer>();
		ArrayList<Integer> cutList = new ArrayList<Integer>();
		Random rand = new Random();
		for(int i=0; i<valueMax; i++){
			randomList.add(i);
		}
		for(int j=0; j<valueMax; j++){
			int tempIdx = rand.nextInt(valueMax);
			//
			int temp = randomList.get(tempIdx);
			int temp2 = randomList.get(j);
			//
			randomList.set(tempIdx,temp2);
			randomList.set(      j,temp);
			//
		}
		for(int s=0; s<sMax; s++){
			cutList.add(randomList.get(s));
		}
		return cutList;
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
		//System.out.println(roundListClone.toString());
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
		//
		iterationStep++;
		//
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
			if(Util.protocolBoolean){
				Util.protocol += "repair .... NEW SOLUTION (games exchange): "+costs+"\n";
				Util.protocol += sol+"\n";
			}
		}
		else if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			System.out.println(change+": "+costs);
			improvement = true;
			if(Util.protocolBoolean){
				Util.protocol += change+": "+costs+"\n";
			}
		}
	}
	//
	private void listNeighborhoodRound(int changeCnt, boolean minNotMax) {
		//ArrayList<Integer> roundList = sol.getRoundListWithCnt();
		ArrayList<Integer> roundList = sol.getCumulativeCostOfRound();
		ArrayList<Integer> list = getSortedListRounds(roundList, minNotMax);
		//
		int sBeginning;
		int sEnding;
		if(!minNotMax){
			sBeginning = 0;
			sEnding = 1;
		}
		else{
			sBeginning = 1;
			sEnding = 0;
		}
		//
		improvement = true;
		//
		while(improvement){
			//
			improvement = false;
			//
			for(int round=0; round<list.size(); round++){
			//
			int sIdx = list.get(round);
			ArrayList<Integer> sortedListToRound1 = sol.getCumulativeCostOfAllRound( sIdx );
			ArrayList<Integer> sortedListToRound = getSortedListRounds(sortedListToRound1, minNotMax);
			//
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
		//
		improvement = true;
		//List<Integer> listIdx = getRandomList(copyListOfGames.size());
		while(improvement){
		//
		improvement = false;
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
		iterationStep++;
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
			if(Util.protocolBoolean){
				Util.protocol += "repair .... NEW SOLUTION (games exchange): "+costs+"\n";
				Util.protocol += sol+"\n";
			}
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
			if(Util.protocolBoolean){
				Util.protocol += solPossibleChoice.get(minIdx)+"\n";
			}
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
			System.out.println("Improvement: round move "+roundCnt+": "+costs);
		}
		//
		roundCnt = roundCnt-1;
		}
	}
	
	private void threeRoundsChange(int a, int b, int c, int d){
		//
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(a);
		list.add(b);
		list.add(c);
		list.add(d);
		searchPossibleRoundImprovements(list);
		//
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(a);
		list2.add(c);
		list2.add(b);
		list2.add(d);
		searchPossibleRoundImprovements(list2);
		//
		ArrayList<Integer> list3 = new ArrayList<Integer>();
		list3.add(a);
		list3.add(c);
		list3.add(d);
		list3.add(b);
		searchPossibleRoundImprovements(list3);
		//
		ArrayList<Integer> list4 = new ArrayList<Integer>();
		list4.add(a);
		list4.add(d);
		list4.add(b);
		list4.add(c);
		searchPossibleRoundImprovements(list4);
		//
		ArrayList<Integer> list5 = new ArrayList<Integer>();
		list5.add(a);
		list5.add(d);
		list5.add(c);
		list5.add(b);
		searchPossibleRoundImprovements(list5);
		//
		ArrayList<Integer> list6 = new ArrayList<Integer>();
		list6.add(a);
		list6.add(b);
		list6.add(d);
		list6.add(c);
		searchPossibleRoundImprovements(list6);
		//
	}
	private void searchPossibleRoundImprovements(ArrayList<Integer> list){
		//
		int costsOrg = sol.getCumulativeCost();
		List<List<Game>> oldSolution = sol.deepCopy();
		List<List<Game>> listRounds = new ArrayList<List<Game>>();
		List<Integer> idxRounds = new ArrayList<Integer>();
		String change = "Rounds: ";
		//
		iterationStep++;
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
		if(orgSolutionFalse && !badConstraint){
			System.out.println("repair .... NEW SOLUTION (round exchange): "+costs);
			System.out.println(sol);
			orgSolutionFalse = (sol.checkConstraint());
			if(Util.protocolBoolean){
				Util.protocol += "repair .... NEW SOLUTION (games exchange): "+costs+"\n";
				Util.protocol += sol+"\n";
			}
		}
		else if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			//System.out.println("possible "+costs);
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
							//
							ArrayList<Integer> list = new ArrayList<Integer>();
							list.add(idx);
							list.add(idx1);
							list.add(idx2);
							searchPossibleRoundImprovements(list);
							//
							ArrayList<Integer> list2 = new ArrayList<Integer>();
							list2.add(idx2);
							list2.add(idx);
							list2.add(idx1);
							searchPossibleRoundImprovements(list2);
							//
							ArrayList<Integer> list3 = new ArrayList<Integer>();
							list3.add(idx1);
							list3.add(idx2);
							list3.add(idx);
							searchPossibleRoundImprovements(list3);
							//
							ArrayList<Integer> list4 = new ArrayList<Integer>();
							list4.add(idx);
							list4.add(idx2);
							list4.add(idx1);
							searchPossibleRoundImprovements(list4);
							//
							ArrayList<Integer> list5 = new ArrayList<Integer>();
							list5.add(idx2);
							list5.add(idx1);
							list5.add(idx);
							searchPossibleRoundImprovements(list5);
							//
							ArrayList<Integer> list6 = new ArrayList<Integer>();
							list6.add(idx1);
							list6.add(idx);
							list6.add(idx2);
							searchPossibleRoundImprovements(list6);
							//
				}	}	}
			}
			else if(cntChange==3){
				for(int idx=0; idx<cntRound; idx++){
					for(int idx1=idx+1; idx1<cntRound; idx1++){
						for(int idx2=idx1+1; idx2<cntRound; idx2++){
							for(int idx3=idx2+1; idx3<cntRound; idx3++){
								//
								threeRoundsChange(idx, idx1, idx2, idx3);
								threeRoundsChange(idx1, idx, idx2, idx3);
								threeRoundsChange(idx2, idx1, idx, idx3);
								threeRoundsChange(idx3, idx1, idx2, idx);
								//
				}	}	}	}
			}
			else{
				for(int idx=0; idx<sol.getRoundsNum(); idx++){
					moveListIdx(idx);
				}
			}
			improvement = setBestImprovement();
		}
	}

	private void searchRoundImprovement(ArrayList<Integer> list){
		//
		iterationStep++;
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
			if(Util.protocolBoolean){
				Util.protocol += "repair .... NEW SOLUTION (games exchange): "+costs+"\n";
				Util.protocol += sol+"\n";
			}
		}
		else if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			System.out.println("Improvement "+change+": "+costs);
			improvement = true;
			if(Util.protocolBoolean){
				Util.protocol += "Improvement "+change+": "+costs+"\n";
			}
		}
	}
	
	private void changeGame(Game game, int ch, int ch2){
		if(game.a==ch){
			game.a = ch2;
		}
		else if(game.a==ch2){
			game.a = ch;
		}
		//
		if(game.b==ch){
			game.b = ch2;
		}
		else if(game.b==ch2){
			game.b = ch;
		}
	}
	
	private void changeTeams(){
		//
		int costsOrg = sol.getCumulativeCost();
		//
		for(int ch=0; ch<sol.getCitiesNum(); ch++){
			for(int ch2=ch+1; ch2<sol.getCitiesNum(); ch2++){
				//
				Game game = null;
				for (int round = 0; round < sol.getSolution().size(); round++) {
					for (int city = 0; city < sol.getSolution().get(0).size(); city++) {
						game = sol.getSolution().get(round).get(city);
						changeGame(game, ch, ch2);
					}
				}
				int costs = sol.getCumulativeCost();
				//
				boolean badConstraint = sol.checkConstraint();
				if((costsOrg<=costs) || badConstraint){
					for (int round = 0; round < sol.getSolution().size(); round++) {
						for (int city = 0; city < sol.getSolution().get(0).size(); city++) {
							game = sol.getSolution().get(round).get(city);
							changeGame(game, ch2, ch);
						}
					}
				}
				else{
					System.out.println("Improvement "+ch+" - "+ch2+": "+costs);
					System.out.println(sol);
					if(Util.protocolBoolean){
						Util.protocol += "Improvement "+ch+" - "+ch2+": "+costs+"\n";
					}
				}
		}
		}
	}
	
	private void changeTeams2(int cho){
		//
		int costsOrg = sol.getCumulativeCost();
		//
		for(int ch=0; ch<sol.getCitiesNum(); ch++){
			for(int ch2=ch+1; ch2<sol.getCitiesNum(); ch2++){
				for(int ch3=ch2+1; ch3<sol.getCitiesNum(); ch3++){
				//
				Game game = null;
				for (int round = 0; round < sol.getSolution().size(); round++) {
					for (int city = 0; city < sol.getSolution().get(0).size(); city++) {
						game = sol.getSolution().get(round).get(city);
						if(cho==0){
							changeGame(game, ch, ch2);
							changeGame(game, ch, ch3);
						}
						else{
							changeGame(game, ch, ch2);
							changeGame(game, ch2, ch3);
						}
					}
				}
				int costs = sol.getCumulativeCost();
				//
				boolean badConstraint = sol.checkConstraint();
				if((costsOrg<=costs) || badConstraint){
					for (int round = 0; round < sol.getSolution().size(); round++) {
						for (int city = 0; city < sol.getSolution().get(0).size(); city++) {
							game = sol.getSolution().get(round).get(city);
							if(cho==0){
								changeGame(game, ch3, ch);
								changeGame(game, ch2, ch);
							}
							else{
								changeGame(game, ch3, ch2);
								changeGame(game, ch2, ch);
							}
						}
					}
				}
				else{
					System.out.println("Improvement "+ch+" - "+ch2+" - "+ch3+": "+costs);
					System.out.println(sol);
					if(Util.protocolBoolean){
						Util.protocol += "Improvement "+ch+" - "+ch2+" - "+ch3+": "+costs+"\n";
					}
				}
		}
			}
		}
	}
	
	private void getStatistic(ArrayList<Integer> al){
		int average = 0;
		for(int i=0; i<al.size(); i++){
			average += al.get(i);
		}
		average = average/al.size();
		int std = 0;
		for(int i=0; i<al.size(); i++){
			int temp = (average-al.get(i))*(average-al.get(i));
			std += temp;
		}
		double var = Math.sqrt( std/(al.size()-1) );
		//
		System.out.println(average);
		System.out.println(var);
	}
	
	public void neighborhoodRoundsTests(int neighChoice){	
		//
		orgSolutionFalse = sol.checkConstraint();
		//
		if(neighChoice==0){ //random neighbor
			int neigh = sol.getRoundsNum();
			int times = 100;
			//for(int i=2; i<neigh; i++){
				//System.out.println(i);
			ArrayList<Integer> al = new ArrayList<Integer>();
			for(int i=0; i<100; i++){
				List<List<Game>> oldSolution = sol.deepCopy();
				randomNeighborhoodRounds(6, times);
				al.add(sol.getCumulativeCost());
				sol.setSolution(oldSolution);
			}
			getStatistic(al);
			//}
			System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost());
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+"\n";
			}
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			for(int i=0; i<sol.getRoundsNum(); i++){
				listNeighborhoodRound(i,minNotMax);
			}
			System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+", Iterationsteps: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+", Iterationsteps: "+iterationStep+"\n";
			}
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			//
			int cntChanges = 6;
			for(int i=0; i<cntChanges; i++){
				bestNeighborhoodRounds(i);
			}
			System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+", Iterationsteps: "+iterationStep);
			
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+", Iterationsteps: "+iterationStep+"\n";
			}
			/*
			for(int i=0; i<sol.getRoundsNum(); i++){
				moveListIdx(i);
			}
			
			for(int i=0; i<sol.getRoundsNum(); i++){
				//for(int i=sol.getRoundsNum(); i>0; i--){
					//moveListIdx(i);
					bestNeighborhoodRounds(1);
					List<ArrayList<Integer>> listOfGames = sol.getGameList();
					bestNeighborhoodGame(listOfGames,5);
				}*/
		}
		//
		
		//
	}
	
	public void neighborhoodGamesTests(int neighChoice){
		//
		orgSolutionFalse = sol.checkConstraint();
		//
		List<ArrayList<Integer>> listOfGames = sol.getGameList();
		if(neighChoice==0){ //random neighbor
			//ArrayList<Integer> sortList = getRandomListWithoutRepeats(listOfGames.size());
			//ArrayList<Integer> sortList = getRandomList(listOfGames.size());
			//listNeighborhoodGame(listOfGames, sortList);
			//listNeighborhoodGameRandom(List<ArrayList<Integer>> listOfGames, int n, int changeCnt, int sRandom)
			/*
			for(int i=0; i<5; i++){
				listNeighborhoodGameRandom(listOfGames, listOfGames.size(), i, 15000);
			}
			*/
			//
			int times = 100;
			ArrayList<Integer> al = new ArrayList<Integer>();
			for(int i=0; i<100; i++){
				List<List<Game>> oldSolution = sol.deepCopy();
				for(int j=1; j<4; j++){
					listNeighborhoodGameRandom(listOfGames, listOfGames.size(), j, 1000);
				}
				al.add(sol.getCumulativeCost());
				sol.setSolution(oldSolution);
			}
			getStatistic(al);
			
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+", Iterations: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+", Iterations: "+iterationStep+"\n";
			}
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			List<List<Game>> oldSolution = sol.deepCopy();
			//List<ArrayList<Integer>> listOfGamesCnt = sol.getGameListWithCnt();
			List<ArrayList<Integer>> listOfGamesCnt = sol.getGameListCosts();
			for(int i=0; i<5; i++){
				listNeighborhoodGame(listOfGamesCnt,minNotMax,i);
				System.out.println("... Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+", Iterations: "+iterationStep);
				sol.setSolution(oldSolution);
			}
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+", Iterations: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+", Iterations: "+iterationStep+"\n";
			}
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			//bestNeighborhoodGame(listOfGames,1);
			//bestNeighborhoodGame(listOfGames,5);
			List<List<Game>> oldSolution = sol.deepCopy();
			for(int i=0; i<5; i++){
				bestNeighborhoodGame(listOfGames,i);
				System.out.println(".... Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterations: "+iterationStep);
				sol.setSolution(oldSolution);
			}
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterations: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterations: "+iterationStep+"\n";
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
	public void neighborhoodRounds(int neighChoice, int from, int to){	
		//
		orgSolutionFalse = sol.checkConstraint();
		//
		if(neighChoice==0){ //random neighbor
			int times = 1000;
			for(int neighCnt=from; neighCnt<to; neighCnt++){
				randomNeighborhoodRounds(neighCnt, times);
			}
			System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+times);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+times+"\n";
			}
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			
			for(int neighCnt=from; neighCnt<to; neighCnt++){
				listNeighborhoodRound(neighCnt,minNotMax);
			}
			System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep+"\n";
			}
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			for(int neighCnt=from; neighCnt<to; neighCnt++){
				bestNeighborhoodRounds(neighCnt);
			}
			System.out.println("Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (rounds) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep+"\n";
			}
		}
	}
	/*
	 * neighbor games
	 * 0: random neighborhood
	 * 1: next improvement
	 * 2: best improvement
	 */
	public void neighborhoodGames(int neighChoice, int from, int to){
		orgSolutionFalse = sol.checkConstraint();
		List<ArrayList<Integer>> listOfGames = sol.getGameList();
		
		if(neighChoice==0){ //random neighbor
			int times = 1000;
			for(int neighCnt=from; neighCnt<to; neighCnt++){
					listNeighborhoodGameRandom(listOfGames, listOfGames.size(), neighCnt, times);
			}
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+times);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+times+"\n";
			}
		}
		else if(neighChoice==1){//next improvement: search in a specified order, take first solution, which is better than x
			List<List<Game>> oldSolution = sol.deepCopy();
			List<ArrayList<Integer>> listOfGamesCnt = sol.getGameListCosts();
			for(int neighCnt=from; neighCnt<to; neighCnt++){
				listNeighborhoodGame(listOfGamesCnt,minNotMax,neighCnt);
			}
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep+"\n";
			}
		}
		else if(neighChoice==2){//best improvement: search through N completely and take the best solution
			for(int neighCnt=from; neighCnt<to; neighCnt++){
				bestNeighborhoodGame(listOfGames,neighCnt);
			}
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep+"\n";
			}
		}
	}
	
	public void neighborhoodTeams(int neighChoice){
		if(neighChoice==1){
			changeTeams();
		}
		else{
			changeTeams2(0);
			changeTeams2(1);
		}
			
		//}
	}
	
}
