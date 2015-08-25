package se.chalmers.fleetspeak.network.udp;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JitterBuffer<E> implements BufferedAudioStream{

	private JitterBufferQueue<E> buffer;
	private long lastestReadtimestamp;
	private Boolean ready, empty = true;
	private int currentSize;
	private final int BUFFERSIZE = 3;
	private Logger logger;

	private long bufferTime;
	
	/**
	 * Constructs a JitterBuffer which delays the media in favor of consistency
	 * where packets have more time to arrive and can be sorted in the right order.
	 * Higher buffer time will improve consistency on poor networks but will add a
	 * delay to the processing of the media equal to the buffer time. 
	 * @param bufferTime The delay the JitterBuffer has to work with in milliseconds.
	 */
	public JitterBuffer(long bufferTime) {
		logger = Logger.getLogger("Debug");
		this.bufferTime = bufferTime;
	}
	
	public JitterBuffer(){
		buffer = new JitterBufferQueue<E>();
		lastestReadtimestamp = 0;
		empty = true;
		currentSize = 0;
		logger = Logger.getLogger("Debug");
	}

	public void write(RTPPacket packet) {
		
	}
	
	public void write(E e, long timestamp){
		if(timestamp > lastestReadtimestamp){
			logger.log(Level.FINEST, "Item put in buffer");
			buffer.offer(e, timestamp);
			currentSize++;
			if(currentSize >= BUFFERSIZE) {
				empty = false;
				logger.log(Level.FINEST,"JitterBuffer now allows reads");
			}
		}else{
			logger.log(Level.FINEST, "Item arrived to late, got dropped");
		}
	}

	public byte[] read() {
		if (!ready) {
			return null;
		}
		return null;
	}
	
	public E read2(){
		if(!empty){
			currentSize--;
			if(currentSize == 0){
				empty = true;
			}
			JitterBufferQueue<E>.Node n = buffer.poll();
			lastestReadtimestamp = n.timestamp;

			return n.e;
		}
		return null;
	}








}
