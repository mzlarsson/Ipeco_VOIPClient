package se.chalmers.fleetspeak.newgui.connection;

public interface ConnectionListener {

	public void onConnect();
	public void onConnectionFailure(String msg);
	
}
