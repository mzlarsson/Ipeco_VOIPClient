package se.chalmers.fleetspeak.core;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;
import se.chalmers.fleetspeak.util.Command;

/**
 * A sort of instantiated factory for the client creation process.
 * It makes sure the new connection is authenticated to connect before
 * adding it as a client on the server.
 *
 * @author Patrik Haar
 */
public class ClientCreator implements AuthenticatorListener{

	private List<ClientAuthenticator> authenticators;
	private Logger logger;
	private Building building;

	//TODO Add to config file
	private int targetRoom = 1;

	/**
	 * Constructor for a ClientCreator.
	 */
	public ClientCreator(Building building) {
		this.building = building;
		logger = Logger.getLogger("Debug");
		authenticators = Collections.synchronizedList(new ArrayList<ClientAuthenticator>());
	}

	/**
	 * Starts the authentication process to check if the new connection is a valid user.
	 * @param clientSocket
	 */
	public void addNewClient(Socket clientSocket) {
		ClientAuthenticator ca = new ClientAuthenticator(new TCPHandler(clientSocket));
		authenticators.add(ca);
		ca.addAuthenticatorListener(this);
		ca.start();
	}

	/**
	 * Shuts down all connections in the process of authentication.
	 */
	public void terminate() {
		for (ClientAuthenticator ca : authenticators) {
			ca.terminate();
		}
		authenticators.clear();
	}

	private void establishUDPConnection() {

	}

	@Override
	public void authenticationSuccessful(Object authorizedObject, Authenticator authenticator) {
		if (authenticator.getClass() == ClientAuthenticator.class) {
			ClientAuthenticator ca = (ClientAuthenticator)authenticator;
			if (authorizedObject != null) {
				UserInfo ui = (UserInfo)authorizedObject;
				TCPHandler tcph = ca.getTCPHandler();
				Client client = new Client(ui.getID(), ui.getAlias(), ca.getTCPHandler().getInetAddress(), tcph);
				tcph.sendCommand(new Command("authenticationResult", true, "Successful authentication"));
				logger.log(Level.INFO, "A new person joined");
				building.addClient(client, targetRoom);
				//TODO this should be checked together with name/pwd
				/*if (RoomHandler.getInstance().findClient(ui.getID()) == null) {

				} else {
					logger.log(Level.INFO, "User with id: " + ui.getID() + " allready exists on the server and was denied access.");
				}*/
			} else {
				logger.log(Level.WARNING, "ClientAuthenticator: " + ca + " accepted a user not in the database.");
			}
			authenticators.remove(ca);
		} else {
			logger.log(Level.WARNING, "Unknown '" + authenticator.getClass() + "' was found when '" + ClientAuthenticator.class + "' was expected.");
		}
	}

	@Override
	public void authenticationFailed(String errorMsg, Authenticator authenticator) {
		if (authenticator.getClass() == ClientAuthenticator.class) {
			ClientAuthenticator ca = (ClientAuthenticator)authenticator;
			ca.getTCPHandler().sendCommand(new Command("authenticationResult", false, errorMsg));
			ca.terminate();
			logger.log(Level.FINER, errorMsg);
			authenticators.remove(ca);
		} else {
			logger.log(Level.WARNING, "Unknown '" + authenticator.getClass() + "' was found when '" + ClientAuthenticator.class + "' was expected.");
		}
	}
}
