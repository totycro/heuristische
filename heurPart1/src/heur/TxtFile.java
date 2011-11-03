package heur;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * read textfile to int Array
 */
public class TxtFile {
	
	private ArrayList<Integer> list;
	private int[][] dist;
	private String data;
	private int n;
	
	public TxtFile(String data){
		this.data = data;
		this.list = getTxtFromFile(data);
		this.dist = getMatrix(list);
	}
	
	/*
	 * read numbers from textfile (data)
	 */
	private ArrayList<Integer> getTxtFromFile(String data){
		//
		ArrayList<Integer> list = new ArrayList<Integer>();
		//
		try {
			//
			BufferedReader bffR = new BufferedReader(new FileReader(data));
			String st = null;
			//
			while ( (st = bffR.readLine()) != null ) {
				//
				String numb = "";
				int singleNum = 0;
				//
				for( int cnt=0; cnt<st.length(); cnt++){
					char stPoint = st.charAt(cnt);
					if (stPoint==' ') {
						if(!numb.isEmpty()){
							singleNum = Integer.parseInt(numb);
    						list.add(singleNum);
							numb = "";
						}
					}
					else{
						numb = numb+stPoint;
					}
				}
				//
				if(!numb.isEmpty()){
					singleNum = Integer.parseInt(numb);
					list.add(singleNum);
					numb = "";
				}
				//
			}
			//
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	//
	/*
	 * change ArrayList to int Array
	 * n: teams
	 */
	private int[][] getMatrix(ArrayList<Integer> list){
			this.n = (int) Math.sqrt(list.size());
			int [][] matrix = new int [n][n];
			int cnt = 0;
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					matrix[i][j] = list.get(cnt);
					cnt = cnt+1;
				}
			}
			return matrix;
	}
	//sort matrix per row
	public int[][] sortM(int[][] distM){
		if(distM.length>0){
			int[] distSort = distM[0];
			for(int j=0; j<distSort.length; j++){
				distSort = distM[j];
				Arrays.sort(distSort);
			}
		}
		return distM;
	}
	
	public int[][] getDist() {
		return dist;
	}

	public String getData() {
		return data;
	}
	
	public int getN(){
		return n;
	}
}
