package heur;

public class proj1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String data = "data8.txt";
		TxtFile txf = new TxtFile(data);
		int[][] distM = txf.getDist();
		int n = txf.getN();
		//
	}

}
