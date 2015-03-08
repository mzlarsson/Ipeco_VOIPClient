package se.chalmers.fleetspeak.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A Class for creating a new unique Ports.
 * @author Patrik Haar
 * @version 1.0
 */
public class PortFactory {

	private int currentPort = 8868;
	private static PortFactory instance;
	private Queue<Integer> recycledPorts = new LinkedList<Integer>();
	
	/**
	 * Creates a new PortFactory
	 */
	private PortFactory() {
	}
	
	/**
	 * Get the instance of the PortFactory
	 * @return PortFactory - the instance of the PortFactory
	 */
	public static PortFactory getInstance() {
		if (instance == null) {
			instance = new PortFactory();
		}
		return instance;
	}
	
	/**
	 * Finds a free port.
	 * @return A free port.
	 */
	public synchronized int getPort() {
		if(!recycledPorts.isEmpty()){
			return recycledPorts.poll();
		}
		int port = currentPort;
		currentPort += 2;
		return port;
	}

	/**
	 * Frees the port to be used by someone else.
	 * @return the old portNbr.
	 */
	public synchronized void freePort(int portNbr) {
		recycledPorts.add(portNbr);
	}
}