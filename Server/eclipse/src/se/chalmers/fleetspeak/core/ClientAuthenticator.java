package se.chalmers.fleetspeak.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.database.DatabaseCommunicator;
import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;

/**
 * A class that will communicate with a connection until it has successfully
 * been authorized as a client.
 *
 * @author Patrik Haar
 */
public class ClientAuthenticator implements Authenticator, CommandHandler{

	private TCPHandler tcp;

	private List<AuthenticatorListener> listeners;

	private int randomNumber;

	/**
	 * Constructor for a ClientAuthenticator.
	 * @param clientSocket The socket with the new connection to authorize.
	 */
	public ClientAuthenticator(TCPHandler tcp) {
		SecureRandom rand = new SecureRandom();
		randomNumber = rand.nextInt();
		listeners = new ArrayList<AuthenticatorListener>();
		this.tcp = tcp;
		tcp.setCommandHandler(this);
		tcp.start();
	}

	/**
	 * Starts the authentication process.
	 */
	public void start() {

		tcp.sendCommand("{\"command\":\"sendAuthenticationDetails\","
				+ "\"work\":\"" + randomNumber + "\"}");
	}
	/**
	 * Checks if the response from the connection matches with the information in the database.
	 * @param authCommand The response from the connection.
	 * @return true if accepted, false if not.
	 */
	private boolean authenticate(String authCommand) {
		try {
			JSONObject command = new JSONObject(authCommand);
			int work = command.getInt("work");
			UserInfo user = DatabaseCommunicator.getInstance().findUser(command.getString("username"));
			if (user != null && work == randomNumber) {
				for (AuthenticatorListener al : listeners) {
					al.authenticationSuccessful(user, this);
				}
				return true;
			} else {
				failedAuthentication("Unknown username and password combination");
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	private void failedAuthentication(String errorMsg) {
		for (AuthenticatorListener al : listeners) {
			al.authenticationFailed(errorMsg, this);
		}
	}

	/**
	 * Returns the connection that is pending authorization.
	 * @return The TCPHandler holding the connection.
	 */
	public TCPHandler getTCPHandler() {
		return tcp;
	}

	/**
	 * Shuts down the active connection in the process of authorization
	 */
	public void terminate() {
		tcp.terminate();
	}

	@Override
	public void addAuthenticatorListener(AuthenticatorListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeAuthenticatorListener(AuthenticatorListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void handleCommand(String c) {
		if(c != null) {
			authenticate(c);
		}
	}
}
