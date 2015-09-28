package se.chalmers.fleetspeak.sound;

public interface Decoder {

	public byte[] decode(byte[] indata);
	public void terminate();
	
}
