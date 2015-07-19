package se.chalmers.fleetspeak.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Class for creating a new unique ID.
 * @author Patrik Haar
 * @version 1.0
 */
public class IDFactory {

	private int currentID = 1;
	private static IDFactory instance;
	private LinkedList<Integer> idsInUse = new LinkedList<Integer>();
	private Queue<Integer> recycledIDs = new LinkedList<Integer>();

	private Logger logger = Logger.getLogger("Debug");;
	/**
	 * Creates a new IDFactory
	 */
	private IDFactory() {
	}
	/**
	 * Get the instance of the IDFactory
	 * @return IDFactory - the instance of the IDFactory
	 */
	public static IDFactory getInstance() {
		if (instance == null) {
			instance = new IDFactory();
		}
		return instance;
	}
	
	/**
	 * Creates a unique ID.
	 * @return The unique ID.
	 */
	public synchronized int getID() {
		if(!recycledIDs.isEmpty()){
			int recycledID = recycledIDs.poll();
			idsInUse.add(recycledID);
			return recycledID;
		}
		idsInUse.add(currentID);
		return currentID++;
	}

	/**
	 * Frees the ID to be used by someone else.
	 * @return the old ID.
	 */
	public synchronized void freeID(Integer id) {
		if (idsInUse.remove(id)) {
			recycledIDs.add(id);
		} else {
			logger.log(Level.FINE,"Tried to free a ID that was not in use.");
		}
	}
}