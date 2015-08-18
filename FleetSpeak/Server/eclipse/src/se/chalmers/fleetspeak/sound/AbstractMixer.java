package se.chalmers.fleetspeak.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic structure layer for the mixers. Contains basic functionality.
 * @author Matz Larsson
 * @version 1.0
 *
 */

public abstract class AbstractMixer implements Mixer{

	
	private List<BufferedStream> streams;
	
	protected AbstractMixer(){
		streams = new ArrayList<BufferedStream>();
	}

	/**
	 * Adds a stream to this mixer. The stream will be requested for data on calls to getMixed().
	 * @see se.chalmers.fleetspeak.sound.SimpleMixer.getMixed(int)
	 * @param stream The stream to add
	 */
	@Override
	public void addStream(BufferedStream stream) {
		if(stream != null){
			streams.add(stream);
		}
	}

	/**
	 * Removes the stream from the mixer. No more data will be requested from it.
	 * NOTE: This call does NOT close the stream.
	 */
	@Override
	public void removeStream(BufferedStream stream) {
		if(stream != null){
			streams.remove(stream);
		}
	}

	/**
	 * Collects the data needed from the streams for the mixing.
	 * @param nbrOfBytes The number of bytes to retrieve
	 * @return The data from the streams, ready to be mixed.
	 */
	protected byte[][] getData(int nbrOfBytes){
		//Save data from streams
		byte[][] data = new byte[streams.size()][nbrOfBytes];
		for(int i = 0; i<streams.size(); i++){
			data[i] = streams.get(i).getData(nbrOfBytes);
		}
		
		return data;
	}
	
	/**
	 * Retrieves the current streams registered to the mixer. This method is recommended to use
	 * to change the implementation of getData, for encoding/decoding etc.
	 * @return The current registered streams.
	 */
	protected List<BufferedStream> getStreams(){
		return streams;
	}
}
