package heur;

import heur.Solution.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NeighborhoodMerg2 {

	private Solution sol;
	private boolean orgSolutionFalse;
	private ArrayList<List<List<Game>>> solPossible = new ArrayList<List<List<Game>>>();
	private ArrayList<String> solPossibleChoice = new ArrayList<String>();
	private boolean improvement;
	private int iterationStep;
	private boolean minNotMax = false;
	private int currentCosts = Integer.MAX_VALUE;
	
	public NeighborhoodMerg2(Solution solution){
		this.sol = solution;
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
	
	/*
	 * BEST
	 */
	private void searchPossibleGamesImprovements(List<ArrayList<Integer>> list, ArrayList<Integer> idxList){
		//
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
		else if((currentCosts<=costs) || badConstraint){
			//
		}
		else{
			solPossible.add(sol.deepCopy());
			solPossibleChoice.add(change);
		}
		////////////////////////////////////////////////////////////////////////////////
		for(int neighCnt=0; neighCnt<3; neighCnt++){
			bestNeighborhoodRounds(neighCnt);
		}
		//
		sol.setSolution(oldSolution);
		////////////////////////////////////////////////////////////////////////////////
		//
		
	}
	
	private boolean setBestImprovement(){
		if(!solPossible.isEmpty()){
			int minIdx = 0;
			int minIdxCosts = -1;
			for(int s=0; s<solPossible.size(); s++){
				sol.setSolution(solPossible.get(s));
				int currentCosts2 = sol.getCumulativeCost();
				if(s==0){
					minIdxCosts = currentCosts2;
				}
				else{
					if(currentCosts<minIdxCosts){
						minIdx = s;
						minIdxCosts = currentCosts2;
					}
				}
			}
			currentCosts = minIdxCosts;
			sol.setSolution(solPossible.get(minIdx));
			System.out.println("set: "+solPossibleChoice.get(minIdx));
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
			
			listOfGames = sol.getGameList();
			
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
									ArrayList<Integer> idxList = new ArrayList<Integer>();
									idxList.add(idx);
									idxList.add(idx1);
									idxList.add(idx2);
									idxList.add(idx3);
									searchPossibleGamesImprovements(listOfGames, idxList);
				}}}}
			}
			else if(cntChange==5){
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
			else if(cntChange==6){
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
			else if(cntChange==7){
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
			else if(cntChange==8){
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
			else if(cntChange==9){
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
		boolean badConstraintBeg = sol.checkConstraint();
		//
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
		if(badConstraintBeg && !badConstraint){
			//System.out.println("repair .... NEW SOLUTION (round exchange): "+costs);
			//System.out.println(sol);
			badConstraintBeg = (sol.checkConstraint());
		}
		else if((currentCosts<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			//System.out.println("possible "+costs);
			solPossible.add(sol.deepCopy());
			solPossibleChoice.add(change);
			sol.setSolution(oldSolution);
			//currentCosts = costs;
		}
	}
	
	private void bestNeighborhoodRounds(int cntChange){
		
		//System.out.println("Round-Change: "+cntChange);
		int cntRound = sol.getRoundsNum();
		
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
	}

	public void changeTeams(){
		List<List<Game>> oldSolution = sol.deepCopy();
		int costsOrg = sol.getCumulativeCost();
		//
		for(int ch=0; ch<sol.getCitiesNum(); ch++){
			for(int ch2=ch+1; ch2<sol.getCitiesNum(); ch2++){
				//
				Game game = null;
				//
				for (int round = 0; round < sol.getSolution().size(); round++) {
					for (int city = 0; city < sol.getSolution().get(0).size(); city++) {
				//
				game = oldSolution.get(round).get(city);
				///////////////////
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
			}
		int costs = sol.getCumulativeCost();

		boolean badConstraint = sol.checkConstraint();
		if((costsOrg<=costs) || badConstraint){
			sol.setSolution(oldSolution);
		}
		else{
			System.out.println("Improvement "+ch+" - "+ch2+": "+costs);
			//improvement = true;
			if(Util.protocolBoolean){
				Util.protocol += "Improvement "+ch+" - "+ch2+": "+costs+"\n";
			}
		}
		}
		}
	}
	
	/*
	 * neighbor games
	 * 0: random neighborhood
	 * 1: next improvement
	 * 2: best improvement
	 */
	public void neighborhoodMerg(int neighChoice, int from, int to){
		if(neighChoice==1){
			changeTeams();
		}
		else{
			orgSolutionFalse = sol.checkConstraint();
			List<ArrayList<Integer>> listOfGames = sol.getGameList();
			for(int neighCnt=from; neighCnt<to; neighCnt++){
				bestNeighborhoodGame(listOfGames,neighCnt);
			}
			System.out.println("Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep);
			if(Util.protocolBoolean){
				Util.protocol += "Neighborhood (games) "+neighChoice+": "+sol.getCumulativeCost()+" (costs), Iterationsteps: "+iterationStep+"\n";
			}
		}
	}
	
}
