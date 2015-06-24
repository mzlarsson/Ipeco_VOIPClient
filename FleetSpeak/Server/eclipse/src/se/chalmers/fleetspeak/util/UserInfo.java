package se.chalmers.fleetspeak.util;

/**
 * An object holding the information of a user for easier handling
 * of the data.
 *
 * @author Patrik Haar
 */
public class UserInfo {

	private int id;
	private String username, alias;
	
	/**
	 * Create an object contain the information of a user.
	 * @param id The ID of the user.
	 * @param username The unique username of the user.
	 * @param alias The alias of the user.
	 */
	public UserInfo(int id, String username, String alias) {
		this.id = id;
		this.username = username;
		this.alias = alias;
	}
	
	/**
	 * Returns the users ID.
	 * @return The users ID.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Returns the users unique username.
	 * @return The users unique username.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Returns the users alias.
	 * @return The users alias.
	 */
	public String getAlias() {
		return alias;
	}
	
	@Override
	public String toString() {
		return "id: \"" + id + "\" username: \"" + username + "\" alias: \"" + alias + "\"";
	}
}
