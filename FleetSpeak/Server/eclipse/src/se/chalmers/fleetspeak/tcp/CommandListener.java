package se.chalmers.fleetspeak.tcp;

public interface CommandListener {

	void commandChanged(Commands key, Object value);
	
}
