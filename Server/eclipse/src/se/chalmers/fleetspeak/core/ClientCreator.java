package se.chalmers.fleetspeak.core;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import se.chalmers.fleetspeak.database.UserInfo;
import se.chalmers.fleetspeak.network.tcp.TCPHandler;

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
	private AndroidClientCreator andCreator;

	/**
	 * Constructor for a ClientCreator.
	 */
	public ClientCreator(Building building) {
		andCreator = new AndroidClientCreator(building);
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
		ca.setAuthenticatorListener(this);
		ca.start();
	}

	@Override
	public void authenticationSuccessful(String userType, UserInfo authorizedUser, ClientAuthenticator authenticator) {
		switch(userType) {
		case "android":
			andCreator.newAndroidClient(authorizedUser, authenticator.getTCPHandler());
			break;
		default:
			logger.log(Level.WARNING, "Support for " + userType + "-clients are not implemented yet");
		}
		authenticators.remove(authenticator);
	}

	@Override
	public void authenticationFailed(String errorMsg, ClientAuthenticator authenticator) {
		JSONObject json = new JSONObject();
		try {
			json.put("command" , "authenticationresult");
			json.put("result", false);
			json.put("rejection", errorMsg);
			authenticator.getTCPHandler().sendCommand(json.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Caught IOException when sending authenticationresult, false. Message: "
					+ e.getMessage() + " terminating client");
		}
		authenticator.terminate();
		logger.log(Level.FINER, errorMsg);
		authenticators.remove(authenticator);
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
}
