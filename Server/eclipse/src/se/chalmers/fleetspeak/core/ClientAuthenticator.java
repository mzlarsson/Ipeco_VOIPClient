package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.database.DatabaseCommunicator;
import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.Command;

/**
 * A class that will communicate with a connection until it has successfully
 * been authorized as a client.
 *
 * @author Patrik Haar
 */
public class ClientAuthenticator implements Authenticator, CommandHandler{

	private TCPHandler tcp;

	private AuthenticatorListener listener;

	/**
	 * Constructor for a ClientAuthenticator.
	 * @param clientSocket The socket with the new connection to authorize.
	 */
	public ClientAuthenticator(TCPHandler tcp) {
		this.tcp = tcp;
		tcp.setCommandHandler(this);
		tcp.start();
	}

	/**
	 * Starts the authentication process.
	 */
	public void start() {
		tcp.sendCommand(new Command("sendAuthenticationDetails", null, null));
	}
	/**
	 * Checks if the response from the connection matches with the information in the database.
	 * @param authCommand The response from the connection.
	 * @return true if accepted, false if not.
	 */
	private boolean authenticate(Command authCommand) {
		if (authCommand.getCommand().toLowerCase().equals("authenticationdetails")
				&& authCommand.getKey().getClass() == String.class) {
			String username = (String)authCommand.getKey();
			UserInfo user = DatabaseCommunicator.getInstance().findUser(username);
			if (user != null) {
				listener.authenticationSuccessful("android", user, this);
				return true;
			} else {
				failedAuthentication("Unknown username and password combination");
				return false;
			}
		} else {
			failedAuthentication("Response is of an unknown format: " + authCommand.toString());
			return false;
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
	public void handleCommand(Command c) {
		authenticate(c);
	}

	@Override
	public void setAuthenticatorListener(AuthenticatorListener listener) {
		this.listener = listener;
	}
}
