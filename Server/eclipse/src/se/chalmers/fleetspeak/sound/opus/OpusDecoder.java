package se.chalmers.fleetspeak.sound.opus;

import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

import se.chalmers.fleetspeak.sound.Decoder;

public class OpusDecoder implements Decoder{

	private long decoder;
	
	public OpusDecoder() throws OpusException{
		this(Constants.DEFAULT_SAMPLE_RATE);
	}
	
	public OpusDecoder(int sampleRate) throws OpusException{
		decoder = Opus.decoder_create(sampleRate, 1);
		if(decoder == 0){
			throw new OpusException("Could not initiate decoder");
		}
	}
	
	public byte[] decode(byte[] indata){
		byte[] outdata = new byte[Constants.DEFAULT_MIXING_ARRAY_SIZE];
		Opus.decode(decoder, indata, 0, indata.length, outdata, 0, outdata.length, 0);
		return outdata;
	}
	
	public void terminate(){
		Opus.decoder_destroy(decoder);
	}
	
	
	static{
		Opus.assertOpusIsFunctional();
	}
}
