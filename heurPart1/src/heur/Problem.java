package heur;

/**
 * Class for problem specification
 *
 */
public class Problem {
	public final int[][] distM;
	public final int consecHome, consecRoad;
	
	public final Double[][] pheromones;
	
	public Problem(int[][] distM, int consecHome, int consecRoad) {
		this.distM = distM;
		this.consecHome = consecHome;
		this.consecRoad = consecRoad;
		
		this.pheromones = new Double[2 * distM.length][2 * distM.length];
		for (int i=0; i<pheromones.length; i++) {
			for (int j=0; j<pheromones.length; j++) {
				pheromones[i][j] = 1.0;
			}
		}
	}

	public int getCitiesNum() {
		return distM.length;
	}
	
	public int getDistance(int a, int b) {
		return distM[a][b];
	}
	
	

}
