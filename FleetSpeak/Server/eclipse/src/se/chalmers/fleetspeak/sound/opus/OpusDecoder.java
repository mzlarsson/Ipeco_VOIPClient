package se.chalmers.fleetspeak.sound.opus;

public class OpusDecoder implements Decoder{

	private long decoder;
	
	public OpusDecoder() throws OpusException{
		this(8000);
	}
	
	public OpusDecoder(int frequency) throws OpusException{
		decoder = Opus.decoder_create(frequency, 1);
		if(decoder == 0){
			throw new OpusException("Could not initiate decoder");
		}
	}
	
	public byte[] decode(byte[] indata){
		byte[] outdata = new byte[indata.length];
		Opus.decode(decoder, indata, 0, indata.length, outdata, 0, outdata.length, 0);
		return outdata;
	}
	
	public void close(){
		Opus.decoder_destroy(decoder);
	}
	
	
	static{
		Opus.assertOpusIsFunctional();
	}
}
