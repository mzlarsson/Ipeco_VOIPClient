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
	
	public UserInfo(int id, String username, String alias) {
		this.id = id;
		this.username = username;
		this.alias = alias;
	}
	
	public int getID() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getAlias() {
		return alias;
	}
	
	@Override
	public String toString() {
		return "id: \"" + id + "\" username: \"" + username + "\" alias: \"" + alias + "\"";
	}
}
