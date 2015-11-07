package se.chalmers.fleetspeak.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A singleton that handles communication with the database.
 *
 * @author Patrik Haar
 */
public class DatabaseCommunicator {

	private Connection conn;
	private static DatabaseCommunicator instance;
	private Logger logger;
	
	private int tmpIDs = 100;

	private DatabaseCommunicator(){
		logger = Logger.getLogger("Debug");
		conn = DatabaseConnector.initiateConnection();
	}

	/**
	 * Returns an instance of the DatabaseCommunicator.
	 * @return An instance of the DatabaseCommunicator.
	 */
	public static DatabaseCommunicator getInstance() {
		if (instance == null) {
			instance = new DatabaseCommunicator();
		}
		return instance;
	}

	/**
	 * Adds a user to the database.
	 * @param username The unique username of the user.
	 * @param alias The alias of the user.
	 * @param password A password has on the form "<iterations>:<salt(hex)>:<password hash(hex)>"
	 * @return null if successfully added, a string with the error if not.
	 */
	public String addUser(String username, String alias, String password) {
		String error = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO users (username, alias, password) "
					+ "VALUES (?, ?, ?);");
			st.setString(1, username);
			st.setString(2, alias);
			st.setString(3, password);
			int addedUsers = st.executeUpdate();
			if (addedUsers != 1) {
				error = "No errors but " + addedUsers + " users were added instead of 1.";
			}
		} catch (SQLException e) {
			switch (e.getSQLState()) {
			case "23505":
				error = "Username '" + username + "' already exists.";
				break;
			default:
				error = "Failed to add user with an unknown error code: " + e.getSQLState();
				break;
			}
		}
		try {
			st.close();
		} catch (SQLException e) {
			logger.warning("Could not close statement when adding user");
		}
		return error;
	}

	/**
	 * Deletes the user with the given ID.
	 * @param id The ID of the user to be deleted.
	 * @return null if successfully removed, a string with the error if not.
	 */
	public String deleteUser(int id) {
		return deleteUser(Integer.toString(id), true);
	}

	/**
	 * Deletes the user with the given username.
	 * @param username The username of the user to be deleted.
	 * @return null if successfully removed, a string with the error if not.
	 */
	public String deleteUser(String username) {
		return deleteUser(username, false);
	}

	/**
	 * Deletes a user from the database.
	 * @param idOrUsername The identifier of the user to be removed.
	 * @param isID true if the identifier is an ID, false if it is a String.
	 * @return null if successfully removed, a string with the error if not.
	 */
	private String deleteUser(String idOrUsername, boolean isID) {
		String error = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM users WHERE " + (isID?"id":"username") + " = ?;");
			st.setString(1, idOrUsername);
			int deletedUsers = st.executeUpdate();
			if (deletedUsers == 0) {
				error = "The user " + (isID?"with ID ":"") + idOrUsername + " does not exist.";
			} else if (deletedUsers != 1) {
				error = "No errors but " + deletedUsers + " users were deleted instead of 1.";
			}
		} catch (SQLException e) {
			switch (e.getSQLState()) {
			default:
				error = "Failed to delete user with an unknown error code: " + e.getSQLState();
			}
		}
		try {
			st.close();
		} catch (SQLException e) {
			logger.warning("Could not close statement when deleting user");
		}
		return error;
	}

	/**
	 * Finds the information of the user from the database.
	 * @param username The unique username of the user.
	 * @return A UserInfo object with the information of the user if found, null if not found.
	 */
	public UserInfo findUser(String username) {
		if (username.equals("bottenanja")) { //FIXME Temporary implementation for allowing bots.
			return new UserInfo(tmpIDs, username+tmpIDs, username+(tmpIDs++), "", "");
		}
		UserInfo user = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM users WHERE username = ?;");
			st.setString(1, username);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				user = new UserInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
			}
			else {
				logger.log(Level.INFO, "[DatabaseCommunicator]: User with username '"
						+ username + "' was not found in the database.");
			}
			rs.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[DatabaseCommunicator]: Failed to read result from the found user in .findUser().");
		}
		try {
			st.close();
		} catch (SQLException e) {
			logger.warning("Could not close statement when locating user");
		}
		return user;
	}

	/**
	 * Closes all connections and frees all resources used.
	 */
	public void terminate() {
		try {
			if(conn!=null) {
				conn.close();
			}
			instance = null;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[DatabaseCommunicator]: Failed to close the connection to the database.");
		}
	}

	/**
	 * Inner class for establishing the initial connection to the server.
	 *
	 * @author Patrik Haar
	 */
	private static class DatabaseConnector {

		private static Connection initiateConnection() {
			Connection connection = null;
			String url = "jdbc:postgresql://localhost/postgres";
			Properties props = new Properties();
			props.setProperty("user","postgres");
			props.setProperty("password","FleetElit");
			//			props.setProperty("ssl","true");	//TODO When we change it to ssl.
			try {
				connection = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				Logger.getLogger("Debug").log(Level.SEVERE, "[DatabaseConnector]: Failed to establish a connection to the database.");
			}
			return connection;
		}
	}
}
