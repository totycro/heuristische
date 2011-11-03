package heur;

import java.util.Set;

public class Util {
	
	/**
	 * Picks an arbitrary element from the set, removes it and returns it.
	 */
	static public <T> T choose(Set<T> set) {
		T t = set.iterator().next();
		set.remove(t);
		return t;
	}

}
