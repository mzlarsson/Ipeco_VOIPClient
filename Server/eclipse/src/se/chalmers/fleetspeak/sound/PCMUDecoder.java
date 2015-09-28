package se.chalmers.fleetspeak.sound;

/**
 * Since PCM is the raw data we are mixing no decoding is needed.
 *
 * @author Patrik Haar
 */
public class PCMUDecoder implements Decoder {

	@Override
	public byte[] decode(byte[] indata) {
		return indata;
	}
	
	@Override
	public void terminate(){}

}
