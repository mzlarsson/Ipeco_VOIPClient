package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;

public class RTPHandler implements PacketReceiver{

	private UDPHandler udp;
	private JitterBuffer jitter;
	private short seqNumber = 0;

	public RTPHandler(DatagramSocket socket) {
		jitter = new JitterBuffer(60);
		udp = new UDPHandler(socket, jitter.getSoundArraySize() + RTPPacket.HEADER_SIZE);
		udp.setReceiver(this);
		udp.start();
	}
	
	public BufferedAudioStream getBufferedAudioStream() {
		return jitter;
	}
	
	@Override
	public void handlePacket(byte[] packet){
		jitter.write(new RTPPacket(packet));
	}
	
	public void sendPacket(byte[] packet) {
		udp.sendPacket(new RTPPacket(seqNumber++, System.currentTimeMillis(), packet).toByteArraySimple());
	}
}
