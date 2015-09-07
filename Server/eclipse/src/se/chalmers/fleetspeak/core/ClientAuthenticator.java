package se.chalmers.fleetspeak.core;

import java.security.SecureRandom;

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

	private AuthenticatorListener listener;

	private int randomNumber;

	/**
	 * Constructor for a ClientAuthenticator.
	 * @param clientSocket The socket with the new connection to authorize.
	 */
	public ClientAuthenticator(TCPHandler tcp) {
		SecureRandom rand = new SecureRandom();
		randomNumber = rand.nextInt();
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
		int work = 0;
		String username = null;
		try {
			JSONObject command = new JSONObject(authCommand);
			work = command.getInt("work");
			username = command.getString("username");
		} catch (JSONException e) {
			failedAuthentication("Data is not in the correct JSON format");
		}
		if (work==randomNumber) {
			UserInfo user = DatabaseCommunicator.getInstance().findUser(username);
			if (user != null) {
				listener.authenticationSuccessful("android", user, this);
				return true;				
			} else {
				failedAuthentication("Unknown username and password combination");
				return false;
			}
		} else {
			failedAuthentication("Work number did not match");
		}

		return false;

	}

	private void failedAuthentication(String errorMsg) {
		listener.authenticationFailed(errorMsg, this);
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
	public void setAuthenticatorListener(AuthenticatorListener listener) {
		this.listener = listener;
	}
	
	public void handleCommand(String c) {
		if(c != null) {
			authenticate(c);
		}
	}
}
