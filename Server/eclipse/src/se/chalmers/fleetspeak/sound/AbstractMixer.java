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

	
	private List<BufferedAudioStream> streams;
	
	protected AbstractMixer(){
		streams = new ArrayList<BufferedAudioStream>();
	}

	/**
	 * Adds a stream to this mixer. The stream will be requested for data on calls to getMixed().
	 * @see se.chalmers.fleetspeak.sound.SimpleMixer.getMixed(int)
	 * @param stream The stream to add
	 */
	@Override
	public void addStream(BufferedAudioStream stream) {
		if(stream != null){
			streams.add(stream);
		}
	}

	/**
	 * Removes the stream from the mixer. No more data will be requested from it.
	 * NOTE: This call does NOT close the stream.
	 */
	@Override
	public void removeStream(BufferedAudioStream stream) {
		if(stream != null){
			streams.remove(stream);
		}
	}

	/**
	 * Collects the data needed from the streams for the mixing.
	 * @param nbrOfBytes The number of bytes to retrieve
	 * @return The data from the streams, ready to be mixed.
	 */
	protected byte[][] getData(){
		//Save data from streams
		byte[][] data = new byte[streams.size()][];
		for(int i = 0; i<streams.size(); i++){
			data[i] = streams.get(i).read();
			
			if(data[i] == null){
				data[i] = new byte[0];
			}
		}
		
		return data;
	}
	
	/**
	 * Retrieves the current streams registered to the mixer. This method is recommended to use
	 * to change the implementation of getData, for encoding/decoding etc.
	 * @return The current registered streams.
	 */
	protected List<BufferedAudioStream> getStreams(){
		return streams;
	}
}
