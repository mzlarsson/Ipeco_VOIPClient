package se.chalmers.fleetspeak.core;

/**
 * A listener interface for listening to authenticators.
 *
 * @author Patrik Haar
 */
public interface AuthenticatorListener {

	/**
	 * This method will be called by subscribed Authenticators on
	 * a successful authorization.
	 * @param authorizedObject The object that has been authorized
	 * @param authenticator The authenticator that tested the object
	 */
	public void authorizationSuccessful(Object authorizedObject, Authenticator authenticator);
	
	/**
	 * This method will be called by subscribed Authenticators on
	 * a failed authorization.
	 * @param errorMsg The reason for the failure
	 * @param authenticator The authenticator that tested the object
	 */
	public void authorizationFailed(String errorMsg, Authenticator authenticator);
}
