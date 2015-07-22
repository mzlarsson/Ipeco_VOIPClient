package se.chalmers.fleetspeak.core;

/**
 * An interface to allow addition and removal of AuthenticatorListeners
 * in an Authenticator.
 *
 * @author Patrik Haar
 */
public interface Authenticator {

	/**
	 * Adds the given listener as a listener to the Authenticator.
	 * @param listener The AuthenticatorListener to be added.
	 */
	public void addAuthenticatorListener(AuthenticatorListener listener);
	
	/**
	 * Removes the given listener as from the Authenticator.
	 * @param listener The AuthenticatorListener to be removed.
	 */
	public void removeAuthenticatorListener(AuthenticatorListener listener);
}
