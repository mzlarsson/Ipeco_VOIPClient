package se.chalmers.fleetspeak.sound.opus;

import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

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
		Opus.encode(encoder, indata, 0, indata.length, outdata, 0, outdata.length);
		return outdata;
	}
	
	public void close(){
		Opus.encoder_destroy(encoder);
	}
	
	static{
		Opus.assertOpusIsFunctional();
	}
}
