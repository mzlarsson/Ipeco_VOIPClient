package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.sound.BufferedAudioStream;

public class RTPHandler implements PacketReceiver, BufferedAudioStream{

	private UDPHandler udp;
	private JitterBuffer jitter;
	private short seqNumber = 0;
	private BlockingQueue<byte[]> outputBuffer;

	public RTPHandler(DatagramSocket socket) {
		jitter = new JitterBuffer(120);
		outputBuffer = new LinkedBlockingQueue<byte[]>();
		udp = new UDPHandler(socket, jitter.getSoundArraySize() + RTPPacket.HEADER_SIZE);
		udp.setReceiver(this);
		udp.start();
	}
	
	public BufferedAudioStream getBufferedAudioStream() {
		return this;
	}
	
	public BlockingQueue<byte[]> getOutputBuffer(){
		return outputBuffer;
	}
	
	@Override
	public void handlePacket(byte[] packet){
		jitter.write(new RTPPacket(packet));
		//FIXME Temporary test function.
		byte[] b = read();
		if(b!=null) {
			sendPacket(b);			
		}
	}
	
	public void sendPacket(byte[] packet) {
		udp.sendPacket(new RTPPacket(seqNumber++, System.currentTimeMillis(), packet).toByteArraySimple());
	}

	@Override
	public byte[] read() {
		RTPPacket p = jitter.read();
		return p!=null ? p.toByteArraySimple() : null;
	}
}
