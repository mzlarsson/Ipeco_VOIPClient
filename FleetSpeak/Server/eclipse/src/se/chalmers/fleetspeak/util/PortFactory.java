package se.chalmers.fleetspeak.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Class for creating a new unique Ports.
 * @author Patrik Haar
 * @version 1.0
 */
public class PortFactory {

	private int currentPort = 8868;
	private static PortFactory instance;
	private LinkedList<Integer> portsInUse = new LinkedList<Integer>();
	private Queue<Integer> recycledPorts = new LinkedList<Integer>();

	private Logger logger = Logger.getLogger("Debug");;
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
//		if(!recycledPorts.isEmpty()){
//			int recycledPort = recycledPorts.poll();
//			portsInUse.add(recycledPort);
//			return recycledPort;
//		} else {
			int port = currentPort;
			currentPort += 2;
			portsInUse.add(port);
			return port;
//		}
	}

	/**
	 * Frees the port to be used by someone else.
	 * @return the old portNbr.
	 */
	public synchronized void freePort(Integer portNbr) {
		if (portsInUse.remove(portNbr)) {
			recycledPorts.add(portNbr);
		} else {
			logger.log(Level.FINE,"Tried to free a port that was not in use.");
		}
	}
}