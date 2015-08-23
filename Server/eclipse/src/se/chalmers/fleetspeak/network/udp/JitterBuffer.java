package se.chalmers.fleetspeak.network.udp;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JitterBuffer<E>{

	private JitterBufferQueue<E> buffer;
	private long lastestReadtimestamp;
	private Boolean empty;
	private int currentSize;
	private final int BUFFERSIZE = 3;
	Logger logger;

	public JitterBuffer(){
		buffer = new JitterBufferQueue<E>();
		lastestReadtimestamp = 0;
		empty = true;
		currentSize = 0;
		logger = Logger.getLogger("Debug");
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


	public E read(){
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
