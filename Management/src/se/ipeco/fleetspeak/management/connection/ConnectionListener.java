package se.ipeco.fleetspeak.management.connection;

public interface ConnectionListener {

	public void onConnect();
	public void onConnectionFailure(String msg);
	
}
