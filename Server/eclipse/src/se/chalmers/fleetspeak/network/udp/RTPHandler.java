package se.chalmers.fleetspeak.network.udp;

import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.sound.AudioType;
import se.chalmers.fleetspeak.sound.BufferedAudioStream;
import se.chalmers.fleetspeak.sound.Decoder;
import se.chalmers.fleetspeak.sound.Encoder;
import se.chalmers.fleetspeak.sound.PCMUDecoder;
import se.chalmers.fleetspeak.sound.PCMUEncoder;
import se.chalmers.fleetspeak.sound.opus.OpusDecoder;
import se.chalmers.fleetspeak.sound.opus.OpusEncoder;
import se.chalmers.fleetspeak.sound.opus.OpusException;

/**
 * This class holds a udp connection and uses it to send and receive
 * RTP traffic.
 * 
 * It treats packets according to their payload type as defined in
 * the enum AudioType.
 * The RTPHandler also keeps AudioType currently in use and encodes
 * all data sent or decodes all data read with the correct codec.
 *
 * @author Patrik Haar
 */
public class RTPHandler implements PacketReceiver, BufferedAudioStream{

	private BlockingQueue<byte[]> outputBuffer;
	private Executor executor;
	private Logger logger;

	private AudioType currAudioType = AudioType.PCMU;
	private UDPHandler udp;
	private JitterBuffer jitter;
	private Encoder encoder;
	private Decoder decoder;
	
	private short seqNumber = 0;

	/**
	 * Creates an RTPHandler from the socket, it does not test this socket
	 * itself but is assuming that it is connected to a client and
	 * initialized correctly.
	 * @param socket The socket connected to the client.
	 */
	public RTPHandler(DatagramSocket socket) {
		logger = Logger.getLogger("Debug");
		jitter = new JitterBuffer(100);
		outputBuffer = new LinkedBlockingQueue<byte[]>();
		udp = new UDPHandler(socket, 1024);	//The 1024 is to make sure the first rtppacket is correctly read.
		udp.setReceiver(this);
		udp.start();

		setAudioType(currAudioType);
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
		RTPPacket p = null;
		try {
			p = new RTPPacket(packet);
		} catch (IllegalArgumentException ex) {
			logger.log(Level.FINEST, "Read packet was not on a valid RTP form: " + ex.getMessage());
		}
		if (p != null) {
			AudioType at = p.payloadType;
			if (at != currAudioType) { // If the AudioType has changed we switch encoder/decoder and drop the packet since it was most likely read wrong.
				setAudioType(at);
			} else {
				jitter.write(p);
//				byte[] b = read();
//				if (b!=null) {
//					System.out.println("We are sending stuff");
//					sendPacket(b);			
//				} else {
//					System.out.println("We are NOT sending stuff");
//				}
			}
		}	
	}

	/**
	 * Sets the encoder, decoder, current AudioType, udp receive size and jitterbuffer size.
	 * @param at The new AudioType.
	 */
	private void setAudioType(AudioType at) {
		switch(at) {
		case PCMU:
			terminateEncoders();
			encoder = new PCMUEncoder();
			decoder = new PCMUDecoder();
			updateCompoponents(at);
			break;
		case OPUS_WB:
			try {
				terminateEncoders();
				encoder = new OpusEncoder(at.getSampleRate(), at.getFrameSize());
				decoder = new OpusDecoder(at.getSampleRate(), at.getFrameSize());
			} catch (OpusException e) {
				logger.log(Level.SEVERE, "OpusException while creating a OpusEncoder/-Decoder "
						+ "for: " + at + " in thread: " + Thread.currentThread().getName());
			}
			updateCompoponents(at);
			break;
		case OPUS_NB:
			try {
				terminateEncoders();
				encoder = new OpusEncoder(at.getSampleRate(), at.getFrameSize());
				decoder = new OpusDecoder(at.getSampleRate(), at.getFrameSize());
			} catch (OpusException e) {
				logger.log(Level.SEVERE, "OpusException while creating a OpusEncoder/-Decoder "
						+ "for: " + at + " in thread: " + Thread.currentThread().getName());
			}
			updateCompoponents(at);
			break;
		default:
			logger.log(Level.WARNING, Thread.currentThread().getName()
					+ " RTP payload-type: " + at + " is not supported");
		}
	}
	
	private void terminateEncoders() {
		if (encoder != null) {	
			encoder.terminate();
		}
		if (decoder != null) {
			decoder.terminate();	
		}
	}

	// Help method for setAudioType.
	private void updateCompoponents(AudioType at) {
		currAudioType = at;
		udp.setPacketSize(RTPPacket.HEADER_SIZE+at.getMaxLength());
		jitter.flush();
		jitter.setFrameSizeMs(at.getTimeBetweenSamples());
	}
	
	/**
	 * Encodes the data to the correct format, puts it into an RTP packet and
	 * sends it to the connected client.
	 * @param data The data in PCM format to be sent.
	 */
	public void sendPacket(byte[] data) {
		udp.sendPacket(new RTPPacket(currAudioType, seqNumber++, System.currentTimeMillis(), encoder.encode(data)).toByteArrayDetailed());
	}

	@Override
	public byte[] read() {
		RTPPacket p = jitter.read();
		return p!=null ? decoder.decode(p.getPayload()) : null;
		//XXX Opus decoding supports null packets which would make it try to predict one,
		//XXX however this would mean we are mixing additional extra data when the client
		//XXX is not sending any. Can write this manually in the java part of the decoder.
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
