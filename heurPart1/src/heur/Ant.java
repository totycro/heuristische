package heur;

public interface Ant {

	/**
	 * Executes the greedy construction heuristic.
	 * @return a solution
	 */
	public abstract Solution execute();

	public abstract Solution execute(long seed);

}