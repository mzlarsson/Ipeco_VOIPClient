package se.chalmers.fleetspeak.newgui.core;

public interface ConnectionListener {

	public void onConnect();
	public void onConnectionFailure(String msg);
	
}
