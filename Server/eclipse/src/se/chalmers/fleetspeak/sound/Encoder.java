package se.chalmers.fleetspeak.sound;

public interface Encoder {

	public byte[] encode(byte[] indata);
	public void close();
	
}
