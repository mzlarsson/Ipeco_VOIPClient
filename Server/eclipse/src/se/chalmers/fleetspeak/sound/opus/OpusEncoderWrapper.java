package se.chalmers.fleetspeak.sound.opus;

import java.util.Arrays;

import se.chalmers.fleetspeak.jni.OpusEncoder;

import se.chalmers.fleetspeak.sound.Encoder;

public class OpusEncoderWrapper implements Encoder{

	private long encoder;
	private int frameSize;

	public OpusEncoderWrapper() throws OpusException{
		this(Constants.DEFAULT_SAMPLE_RATE, Constants.DEFAULT_FRAME_SIZE);
	}

	public OpusEncoderWrapper(int sampleRate, int frameSize) throws OpusException{
		encoder = OpusEncoder.create(sampleRate, 1);
		this.frameSize = frameSize;
		if(encoder == 0){
			throw new OpusException("Could not initiate encoder");
		}
	}

	public byte[] encode(byte[] indata){
		byte[] outdata = new byte[indata.length];
		int length = OpusEncoder.encode(encoder, indata, 0, frameSize, outdata, 0, outdata.length);
		return Arrays.copyOf(outdata, length);
	}

	public void terminate(){
		OpusEncoder.destroy(encoder);
	}

}
