package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;

public class RTPHandler implements PacketReceiver{
	
	private UDPHandler udp;
	private short seqNumber = 0;

	public RTPHandler(DatagramSocket socket) {
		udp = new UDPHandler(socket, 172);
		udp.setReceiver(this);
		udp.start();
	}
	
	@Override
	public void handlePacket(byte[] packet){
		// TODO send to jitterbuffer
		new RTPPacket(packet);
	}
	
	public void sendPacket(byte[] packet) {
		udp.sendPacket(new RTPPacket(seqNumber++, (int)System.nanoTime(), packet).toByteArraySimple());
	}
	
	private class JitterBuffer {
		
		private long bufferTime;
		
		/**
		 * Constructs a JitterBuffer which delays the media in favor of consistency
		 * where packets have more time to arrive and can be sorted in the right order.
		 * Higher buffer time will improve consistency on poor networks but will add a
		 * delay to the processing of the media equal to the buffer time. 
		 * @param bufferTime The delay the JitterBuffer has to work with in milliseconds.
		 */
		public JitterBuffer(long bufferTime) {
			this.bufferTime = bufferTime;
		}
	}
}
