package se.chalmers.fleetspeak.network.udp;

public interface PacketReceiver {

	public void handlePacket(byte[] packet);
}
