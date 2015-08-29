package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;

public class RTPHandler implements PacketReceiver{

	private UDPHandler udp;
	private JitterBuffer jitter;
	private short seqNumber = 0;

	public RTPHandler(DatagramSocket socket) {
		jitter = new JitterBuffer(120);
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
		//FIXME Temporary test function.
		byte[] b = jitter.read();
		if(b!=null) {
			sendPacket(b);			
		}
	}
	
	public void sendPacket(byte[] packet) {
		udp.sendPacket(new RTPPacket(seqNumber++, System.currentTimeMillis(), packet).toByteArraySimple());
	}
}
