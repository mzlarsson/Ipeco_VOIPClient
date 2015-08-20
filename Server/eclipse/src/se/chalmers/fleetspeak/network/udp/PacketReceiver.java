package se.chalmers.fleetspeak.network.udp;

/**
 * Interface for classes able to receive packets in the form of a byte[].
 *
 * @author Patrik Haar
 */
public interface PacketReceiver {

	/**
	 * This method will be called every time a new packet is received.
	 * @param packet the data of the packet.
	 */
	public void handlePacket(byte[] packet);
}
