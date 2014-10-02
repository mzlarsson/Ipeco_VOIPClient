package se.chalmers.fleetspeak.tcp;

public interface CommandListener {

	public void commandChanged(String key, Object value);
	
}
