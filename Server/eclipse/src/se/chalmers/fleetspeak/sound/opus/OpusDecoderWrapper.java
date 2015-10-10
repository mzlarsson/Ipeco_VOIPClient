package se.chalmers.fleetspeak.sound.opus;

import se.chalmers.fleetspeak.jni.OpusDecoder;

import se.chalmers.fleetspeak.sound.Decoder;

public class OpusDecoderWrapper implements Decoder{

	private static final int MAX_DROPPED_IN_A_ROW = 2;

	private long decoder;
	private int frameSize, nbr_predicted;

	public OpusDecoderWrapper() throws OpusException{
		this(Constants.DEFAULT_SAMPLE_RATE, Constants.DEFAULT_FRAME_SIZE);
	}

	public OpusDecoderWrapper(int sampleRate, int frameSize) throws OpusException{
		decoder = OpusDecoder.create(sampleRate, 1);
		this.frameSize = frameSize;
		if(decoder == 0){
			throw new OpusException("Could not initiate decoder");
		}
	}

	public byte[] decode(byte[] indata){
		int FEC = 0;
		if (indata == null) {
			if (nbr_predicted++ < MAX_DROPPED_IN_A_ROW) {
				FEC = 1;
				indata = new byte[0];
			} else {
				return null;
			}
		} else {
			nbr_predicted = 0;
		}
		byte[] outdata = new byte[Constants.DEFAULT_MIXING_ARRAY_SIZE];
		OpusDecoder.decode(decoder, indata, 0, indata.length, outdata, 0, frameSize, FEC);
		return outdata;
	}

	public void terminate(){
		OpusDecoder.destroy(decoder);
	}


}
