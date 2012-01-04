package heur;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Util {
	
	public static boolean CHECK_REPEATERS = true;
	public static String protocol = "";
	public static boolean protocolBoolean = true;
	/**
	 * Picks an arbitrary element from the set, removes it and returns it.
	 */
	static public <T> T choose(Set<T> set) {
		T t = set.iterator().next();
		set.remove(t);
		return t;
	}
	
	/**
	 * Picks the i-th (according to an arbitrary but consistent ordering) arbitrary element from the set, removes it and returns it.
	 */
	static public <T> T choose(Set<T> set, int i) {
		assert (i >= 0);
		assert (i < set.size());
		Iterator<T> iter = set.iterator();
		T t = null;
		for (int j=0; j<=i; j++) {
			t = iter.next();
		}
		set.remove(t);
		return t;
	}
	
	/**
	 * Default logging config is ridiculously bad and unusable, just do it ourselves. 
	 */
	static void setupLogger(Logger log, boolean on) {
		log.setUseParentHandlers(false);
		log.addHandler( new Handler() {
			@Override
			public void publish(LogRecord record) {
				String msg = record.getMessage();
				if (msg.startsWith("\n")) {
					System.err.println();
					msg = msg.substring(1);
				}
				System.err.println(record.getLevel() + ": " + msg);
			}
			@Override
			public void flush() { }
			@Override
			public void close() throws SecurityException { }
		} );
			
		log.setLevel( on ? Level.ALL : Level.OFF );
	}
	
}