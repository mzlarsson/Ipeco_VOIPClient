package se.chalmers.fleetspeak.sound;

import java.util.List;

import se.chalmers.fleetspeak.sound.opus.OpusDecoder;
import se.chalmers.fleetspeak.sound.opus.OpusEncoder;
import se.chalmers.fleetspeak.sound.opus.OpusException;

/**
 * Mixer that handles encoding and decoding of Opus data as well. The BufferedStreams added to instances
 * of this class should contain data that is encoding in the Opus codec. All mixed data will be returned
 * in the encoded Opus format as well.
 * @author Matz Larsson
 * @version 1.0
 */

public class OpusMixer extends SimpleMixer{
	
	private OpusDecoder decoder;
	private OpusEncoder encoder;

	protected OpusMixer(){
		super();
		try {
			decoder = new OpusDecoder();
			encoder = new OpusEncoder();
		} catch (OpusException e) {
			System.out.println("Error while activating Opus functionality: "+e.getMessage());
		}
	}
	
	/**
	 * Collects the data needed from the streams for the mixing.
	 * @param nbrOfBytes The number of bytes to retrieve
	 * @return The data from the streams, ready to be mixed.
	 */
	@Override
	protected byte[][] getData(int nbrOfBytes){
		List<BufferedStream> streams = getStreams();
		byte[][] data = new byte[streams.size()][nbrOfBytes];
		for(int i = 0; i<streams.size(); i++){
			data[i] = decoder.decode(streams.get(i).getData(nbrOfBytes));
		}
		
		return data;
	}

	/**
	 * Returns the mixed byte array of data (Opus encoded) retrieved from all registered BufferedStreams to this mixer. The
	 * data in the BufferedStream is considered to be Opus encoded as well.
	 * @param nbrOfBytes The number of bytes to mix together
	 * @return An array of the mixed encoded Opus data
	 */
	@Override
	public byte[][] getMixed(int nbrOfBytes){
		byte[][] mixed = super.getMixed(nbrOfBytes);
		for(int i = 0; i<mixed.length; i++){
			mixed[i] = encoder.encode(mixed[i]);
		}
		
		return mixed;
	}
	
}
