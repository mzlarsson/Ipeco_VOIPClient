package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.database.DatabaseCommunicator;
import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.PasswordHash;

/**
 * A class that will communicate with a connection until it has successfully
 * been authorized as a client.
 *
 * @author Patrik Haar
 */
public class ClientAuthenticator implements Authenticator, CommandHandler{

	private TCPHandler tcp;
	private Logger logger;

	private AuthenticatorListener listener;

	/**
	 * Constructor for a ClientAuthenticator.
	 * @param clientSocket The socket with the new connection to authorize.
	 */
	public ClientAuthenticator(TCPHandler tcp) {
		logger = Logger.getLogger("Debug");
		this.tcp = tcp;
		tcp.setCommandHandler(this);
		tcp.start();
	}

	/**
	 * Starts the authentication process.
	 */
	public void start() {
		try {
			tcp.sendCommand("{\"command\":\"sendauthenticationdetails\"}");
		} catch (IOException e) {
			failedAuthentication("Client dropped");
		}
	}
	
	/**
	 * Checks if the response from the connection matches with the information in the database.
	 * @param authCommand The response from the connection.
	 */
	private void authenticate(String authCommand) {
		String command = null, username = null, password = null, clientType = null;
		try {
			JSONObject commandString = new JSONObject(authCommand);
			command = commandString.getString("command");
			username = commandString.getString("username");
			password = commandString.getString("password");
			clientType = commandString.getString("clienttype");
		} catch (JSONException e) {
			failedAuthentication("Data is not in the correct JSON format");
		}
		if (command.equalsIgnoreCase("authenticationdetails")) {
			UserInfo user = DatabaseCommunicator.getInstance().findUser(username);
			if (user != null) {
				if (password.equals("")) {  //FIXME This is to temporary ignore passwords.
					listener.authenticationSuccessful(clientType, user, this);					
				} else {
					try {
						if (PasswordHash.validatePassword(password, user.getPassword())) {
							listener.authenticationSuccessful(clientType, user, this);
						} else {
							failedAuthentication("Unknown username and password combination");
						}
					} catch (NoSuchAlgorithmException e) {
						logger.log(Level.SEVERE, "Caught an exception: " + e.getMessage());
					} catch (InvalidKeySpecException e) {
						logger.log(Level.SEVERE, "Caught an exception: " + e.getMessage());
					}
				}
			} else {
				failedAuthentication("Unknown username and password combination");		
			}
		} else {
			failedAuthentication("Unknown command: " + command + " received instead of authenticationdetails");
		}
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
