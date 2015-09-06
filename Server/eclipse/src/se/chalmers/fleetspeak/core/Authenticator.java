package se.chalmers.fleetspeak.core;

/**
 * An interface to allows setting of AuthenticationListener
 * in an Authenticator.
 *
 * @author Patrik Haar
 */
public interface Authenticator {

	/**
	 * Sets the given listener as a listener to the Authenticator.
	 * @param listener The AuthenticatorListener to be set.
	 */
	public void setAuthenticatorListener(AuthenticatorListener listener);
}
