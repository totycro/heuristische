package heur;

/**
 * Class for problem specification
 *
 */
public class Problem {
	public final int[][] distM;
	public final int consecHome, consecRoad;
	
	public Problem(int[][] distM, int consecHome, int consecRoad) {
		this.distM = distM;
		this.consecHome = consecHome;
		this.consecRoad = consecRoad;
	}

	public int getCitiesNum() {
		return distM.length;
	}
	
	public int getDistance(int a, int b) {
		return distM[a][b];
	}

}
