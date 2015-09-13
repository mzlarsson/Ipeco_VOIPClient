package se.chalmers.fleetspeak.sound.opus;

import java.util.Arrays;

import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

import se.chalmers.fleetspeak.sound.Encoder;

public class OpusEncoder implements Encoder{

	private long encoder;
	
	public OpusEncoder() throws OpusException{
		this(Constants.DEFAULT_SAMPLE_RATE);
	}
	
	public OpusEncoder(int sampleRate) throws OpusException{
		encoder = Opus.encoder_create(sampleRate, 1);
		if(encoder == 0){
			throw new OpusException("Could not initiate encoder");
		}
	}
	
	public byte[] encode(byte[] indata){
		byte[] outdata = new byte[indata.length];
		int length = Opus.encode(encoder, indata, 0, indata.length, outdata, 0, outdata.length);
		return Arrays.copyOf(outdata, length);
	}
	
	public void terminate(){
		Opus.encoder_destroy(encoder);
	}
	
	static{
		Opus.assertOpusIsFunctional();
	}
}
