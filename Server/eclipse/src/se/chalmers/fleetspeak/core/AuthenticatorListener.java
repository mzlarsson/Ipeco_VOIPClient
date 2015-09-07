package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.database.UserInfo;

/**
 * A listener interface for listening to authenticators.
 *
 * @author Patrik Haar
 */
public interface AuthenticatorListener {

	/**
	 * This method will be called by subscribed Authenticators on
	 * a successful authentication.
	 * @param userType What kind of user the to be added
	 * @param authenticatedUser The user that has been authenticated
	 * @param authenticator The authenticator that tested the object
	 */
	public void authenticationSuccessful(String userType, UserInfo authenticatedUser, ClientAuthenticator authenticator);

	/**
	 * This method will be called by subscribed Authenticators on
	 * a failed authentication.
	 * @param errorMsg The reason for the failure
	 * @param authenticator The authenticator that tested the object
	 */
	public void authenticationFailed(String errorMsg, ClientAuthenticator authenticator);
}
