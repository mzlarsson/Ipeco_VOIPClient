package se.chalmers.fleetspeak.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic structure layer for the mixers. Contains basic functionality.
 * @author Matz Larsson
 * @version 1.0
 *
 */

public abstract class AbstractMixer implements Mixer{
	
	private Logger logger = Logger.getLogger("Debug");

	private List<BufferedAudioStream> streams;
	private List<BlockingQueue<byte[]>> outStreams;
	
	private volatile boolean running;
	private volatile boolean fetchingData;
	private long nextMixTime;
	private int mixingInterval;
	
	protected AbstractMixer(int mixingInterval){
		this.mixingInterval = mixingInterval;
		
		streams = new ArrayList<BufferedAudioStream>();
		outStreams = new ArrayList<BlockingQueue<byte[]>>();
	}
	
	/**
	 * Makes the mixer move forward: Mixes the input streams and fills the output streams.
	 */
	@Override
	public void run(){
		running = true;
		nextMixTime = System.currentTimeMillis();
		
		while(running){
			//Calc next mixing time
			nextMixTime += mixingInterval;
			
			//Do mixing
			byte[][] data = getMixed();
			for(int i = 0; i<data.length; i++){
				try {
					if(data[i]!=null && outStreams.size()>i){
						BlockingQueue<byte[]> b = outStreams.get(i);
						if(b != null){
							b.put(data[i]);
						}
					}
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "Could not send mixer output because of an InterruptedException: "+e.getMessage(), e);
				}
			}
			
			//Fix timing
			if(nextMixTime>System.currentTimeMillis()){
				try {
					Thread.sleep(nextMixTime-System.currentTimeMillis());
				} catch (InterruptedException e) {
					logger.log(Level.WARNING, "The mixer could not sleep for the appropriate time (InterruptedException)");
				} catch(IllegalArgumentException e){
					logger.log(Level.WARNING, "The mixer sleep time is below 0 (A mixer is slightly behind)");
				}
			}else{
				logger.severe("A mixer is behind!");
			}
		}
	}

	/**
	 * Adds a stream to this mixer. The stream will be requested for data on calls to getMixed().
	 * @see se.chalmers.fleetspeak.sound.SimpleMixer.getMixed()
	 * @param stream The stream to add
	 */
	@Override
	public void addStream(BufferedAudioStream stream, BlockingQueue<byte[]> output) {
		if(stream != null){
			streams.add(stream);
			outStreams.add(output);
		}
	}

	/**
	 * Removes the stream from the mixer. No more data will be requested from it.
	 * NOTE: This call does NOT close the stream.
	 */
	@Override
	public void removeStream(BufferedAudioStream stream) {
		if(stream != null){
			//Do not remove while fetching data
			while(fetchingData){
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			
			//Remove
			int index = streams.indexOf(stream);
			if(index>=0){
				streams.remove(stream);
				outStreams.remove(index);
			}
		}
	}

	/**
	 * Collects the data needed from the streams for the mixing.
	 * @return The data from the streams, ready to be mixed.
	 */
	protected byte[][] getData(){
		//Save data from streams
		fetchingData = true;
		byte[][] data = new byte[streams.size()][];
		for(int i = 0; i<streams.size(); i++){
			BufferedAudioStream b = streams.get(i);
			if(b != null){
				data[i] = b.read();
			}
		}
		fetchingData = false;
		
		return data;
	}
	
	protected int getMaxDataLength(byte[][] data){
		int length = 0;
		for(int i = 0; i<data.length; i++){
			if(data[i] != null && data[i].length>length){
				length = data[i].length;
			}
		}
		
		return length;
	}
	
	
	/**
	 * Retrieves the current streams registered to the mixer. This method is recommended to use
	 * to change the implementation of getData, for encoding/decoding etc.
	 * @return The current registered streams.
	 */
	protected List<BufferedAudioStream> getStreams(){
		return streams;
	}
	
	/**
	 * Returns the mixed byte array of data retrieved from all registered BufferedAudioStreams to the mixer. Each channel
	 * contains the sound of all but the sound stream on that index.
	 * @return An array of the mixed sound
	 */
	protected abstract byte[][] getMixed();
	
	@Override
	public void close(){
		running = false;
	}
}
