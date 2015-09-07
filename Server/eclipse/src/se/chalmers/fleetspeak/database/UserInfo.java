package se.chalmers.fleetspeak.database;

/**
 * An object holding the information of a user for easier handling
 * of the data.
 *
 * @author Patrik Haar
 */
public class UserInfo {

	private int id;
	private String username, alias, password;
	private String[] permissions;
	
	/**
	 * Create an object contain the information of a user.
	 * @param id The ID of the user.
	 * @param username The unique username of the user.
	 * @param alias The alias of the user.
	 * @param password The password of the user.
	 * @param salt The salt for the password.
	 */
	public UserInfo(int id, String username, String alias, String password, String... permissions) {
		this.id = id;
		this.username = username;
		this.alias = alias;
		this.password = password;
		this.permissions = permissions;
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
	
	/**
	 * Returns the users password.
	 * @return The users password.
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Returns the users permissions.
	 * @return The users permissions.
	 */
	public String[] getPermissions() {
		return permissions;
	}
	
	@Override
	public String toString() {
		return "id: \"" + id + "\" username: \"" + username + "\" alias: \"" + alias + "\"";
	}
}
