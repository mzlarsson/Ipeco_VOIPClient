package se.chalmers.fleetspeak.core;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.PermissionLevel;
import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log2;

/**
 * A sort of instantiated factory for the client creation process.
 * It makes sure the new connection is authorized to connect before
 * adding it as a client on the server.
 *
 * @author Patrik Haar
 */
public class ClientCreator implements AuthenticatorListener{

	private List<ClientAuthenticator> authenticators;
	private Logger logger;
	
	/**
	 * Constructor for a ClientCreator.
	 */
	public ClientCreator() {
		logger = Logger.getLogger("Debug");
		authenticators = Collections.synchronizedList(new ArrayList<ClientAuthenticator>());
	}
	
	/**
	 * Starts the authorization-process to check if the new connection is a valid user.
	 * @param clientSocket
	 */
	public void addNewClient(Socket clientSocket) {
		ClientAuthenticator ca = new ClientAuthenticator(new TCPHandler(clientSocket));
		authenticators.add(ca);
		ca.addAuthenticatorListener(this);
		ca.start();
	}

	/**
	 * Shuts down all connections in the process of authorization.
	 */
	public void terminate() {
		for (ClientAuthenticator ca : authenticators) {
			ca.terminate();
		}
		authenticators.clear();
	}
	
	@Override
	public void authorizationSuccessful(Object authorizedObject, Authenticator authenticator) {
		if (authenticator.getClass() == ClientAuthenticator.class) {
			ClientAuthenticator ca = (ClientAuthenticator)authenticator;
			if (authorizedObject != null) {
				UserInfo ui = (UserInfo)authorizedObject;
				if (RoomHandler.getInstance().findClient(ui.getID()) == null) {
					TCPHandler tcph = ca.getTCPHandler();
					Client client = new Client(ui.getID(), ui.getAlias(), ca.getTCPHandler().getInetAddress(), tcph);
					Commands cmds = Commands.getInstance();
					if (cmds.execute(-1, cmds.findCommand("AddUser"), client, PermissionLevel.ADMIN_ALL).wasSuccessful()) { // TODO If not all clients should have ADMIN rights this is the place.
						tcph.sendData(new Command("authorizationResult", true, "Successful authorization"));
						client.start();
						logger.log(Level.INFO, "A new person joined");
					} else {
						tcph.sendData(new Command("authorizationResult", false, "Access denied"));
					}
				} else {
					logger.log(Level.INFO, "User with id: " + ui.getID() + " allready exists on the server and was denied access.");
				}
			} else {
				logger.log(Level.WARNING, "ClientAuthenticator: " + ca + " accepted a user not in the database.");
			}
			authenticators.remove(ca);
		} else {
			logger.log(Level.WARNING, "Unknown '" + authenticator.getClass() + "' was found when '" + ClientAuthenticator.class + "' was expected.");
		}
	}

	@Override
	public void authorizationFailed(String errorMsg, Authenticator authenticator) {
		if (authenticator.getClass() == ClientAuthenticator.class) {
			ClientAuthenticator ca = (ClientAuthenticator)authenticator;
			ca.getTCPHandler().sendData(new Command("authorizationResult", false, errorMsg));
			ca.terminate();
			logger.log(Level.FINER, errorMsg);
			authenticators.remove(ca);
		} else {
			logger.log(Level.WARNING, "Unknown '" + authenticator.getClass() + "' was found when '" + ClientAuthenticator.class + "' was expected.");
		}
	}
}
