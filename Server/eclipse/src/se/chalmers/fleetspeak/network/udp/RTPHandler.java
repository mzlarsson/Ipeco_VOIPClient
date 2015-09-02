package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import se.chalmers.fleetspeak.sound.BufferedAudioStream;

public class RTPHandler implements PacketReceiver, BufferedAudioStream{

	private UDPHandler udp;
	private JitterBuffer jitter;
	private short seqNumber = 0;
	private BlockingQueue<byte[]> outputBuffer;
	private Executor executor;

	public RTPHandler(DatagramSocket socket) {
		jitter = new JitterBuffer(50);
		outputBuffer = new LinkedBlockingQueue<byte[]>();
		udp = new UDPHandler(socket, jitter.getSoundArraySize() + RTPPacket.HEADER_SIZE);
		udp.setReceiver(this);
		udp.start();

		executor = Executors.newSingleThreadExecutor();
		executor.execute(sender);
	}

	public BufferedAudioStream getBufferedAudioStream() {
		return this;
	}

	public BlockingQueue<byte[]> getOutputBuffer(){
		return outputBuffer;
	}

	@Override
	public void handlePacket(byte[] packet){
		RTPPacket p = new RTPPacket(packet);
		if (p.seqNumber!=0 || p.timestamp!=0) {
			jitter.write(new RTPPacket(packet));
//			byte[] b = read();
//			if (b!=null) {
//				System.out.println("We are sending stuff");
//				sendPacket(b);			
//			} else {
//				System.out.println("We are NOT sending stuff");
//			}
		}
	}

	public void sendPacket(byte[] packet) {
		udp.sendPacket(new RTPPacket(seqNumber++, System.currentTimeMillis(), packet).toByteArraySimple());
	}

	@Override
	public byte[] read() {
		RTPPacket p = jitter.read();
		return p!=null ? p.getPayload() : null;
	}

	Runnable sender = () ->{
		byte[] b = null;
		while(udp.isAlive()){
			try{
				b = outputBuffer.take();
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			
			if(b.length>0){
				sendPacket(b);
			}
		}

	};
	
	public void terminate(){
		udp.terminate();
	}
}
