package se.chalmers.fleetspeak.sound;

/**
 * Since PCM is the raw data we are mixing no encoding is needed.
 *
 * @author Patrik Haar
 */
public class PCMUEncoder implements Encoder {

	@Override
	public byte[] encode(byte[] indata) {
		return indata;
	}

	@Override
	public void terminate(){}
}
