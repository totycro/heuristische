package heur;

public class VND {
	
	
	public VND() {
		// TODO: neighborhoods + their next function (next/best)
		
	}
	
	public Solution execute(Solution sol) {
		return execute(sol, 100);
	}
	public Solution execute(Solution sol, int iterations) {
		int i=0;
		
		while (i<iterations) {
			// get next solution w.r.t neighborhood for i
			// neighborhood = neighborhoods[ i % len(neighborhoods) ]
			
			// if better
			// sol = newSol
			// else
			// i++;
		}
		
		return sol;
	}

}
