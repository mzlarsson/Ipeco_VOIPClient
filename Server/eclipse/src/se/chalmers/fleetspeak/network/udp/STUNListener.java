package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;

import se.chalmers.fleetspeak.core.NetworkUser;

public interface STUNListener {

	/**
	 * This will be called on an successful STUN initiation.
	 * @param nu The user associated with the socket. 
	 * @param udp The new STUN-initiated udp socket.
	 */
	public void stunSuccessful(NetworkUser nu, DatagramSocket udp);
	
	/**
	 * This will be called on a failed STUN initiation.
	 * @param nu The user who failed the STUN initiation.
	 * @param error A error message on why it failed.
	 */
	public void stunFailed(NetworkUser nu, String error);
}
