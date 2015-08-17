package se.chalmers.fleetspeak.core;

/**
 * A listener interface for listening to authenticators.
 *
 * @author Patrik Haar
 */
public interface AuthenticatorListener {

	/**
	 * This method will be called by subscribed Authenticators on
	 * a successful authentication.
	 * @param authenticatedObject The object that has been authenticated
	 * @param authenticator The authenticator that tested the object
	 */
	public void authenticationSuccessful(Object authenticatedObject, Authenticator authenticator);

	/**
	 * This method will be called by subscribed Authenticators on
	 * a failed authentication.
	 * @param errorMsg The reason for the failure
	 * @param authenticator The authenticator that tested the object
	 */
	public void authenticationFailed(String errorMsg, Authenticator authenticator);
}
