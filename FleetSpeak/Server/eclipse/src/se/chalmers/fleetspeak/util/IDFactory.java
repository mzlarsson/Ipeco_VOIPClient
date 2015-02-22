package se.chalmers.fleetspeak.util;

import java.util.LinkedList;

/**
 * A Class for creating a new unique ID.
 * @author Patrik Haar
 * @version 1.0
 */
public class IDFactory {

	private int currentID = 1;
	private static IDFactory instance;
	private LinkedList<Integer> recycledIDs = new LinkedList<Integer>();
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
			return recycledIDs.pop();
		}
		
		return currentID++;
	}

	/**
	 * Creates a unique ID.
	 * @return The unique ID.
	 */
	public synchronized void freeID(int id) {
		recycledIDs.addLast(id);
	}
}