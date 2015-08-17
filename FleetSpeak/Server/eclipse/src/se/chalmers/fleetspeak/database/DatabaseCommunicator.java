package se.chalmers.fleetspeak.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
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
	private Logger logger = Logger.getLogger("Debug");


	private DatabaseCommunicator(){
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
	 * @return null if successfully added, a string with the error if not.
	 */
	public String addUser(String username, String alias) {
		String error = null;
		Statement st = null;
		try {
			st = conn.createStatement();
			int addedUsers = doUpdate("INSERT INTO users (username, alias) VALUES ('"
					+ username + "', '" + alias + "');", st);
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
			e.printStackTrace();
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
		Statement st = null;
		try {
			st = conn.createStatement();
			int deletedUsers = doUpdate("DELETE FROM users WHERE "
					+ (isID?"id":"username") + " = '" + idOrUsername + "';", st);
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
			e.printStackTrace();
		}
		return error;
	}

	/**
	 * Finds the information of the user from the database.
	 * @param username The unique username of the user.
	 * @return A UserInfo object with the information of the user if found, null if not found.
	 */
	public UserInfo findUser(String username) {
		UserInfo user = null;
		Statement st = null;
		try {
			st = conn.createStatement();
			ResultSet rs = doQuery("SELECT * FROM users "
					+ "WHERE username = '"+username+"'", st);
			if (rs.next()) {
				user = new UserInfo(rs.getInt(1), rs.getString(2), rs.getString(3));
			}
			else {
				logger.log(Level.INFO, "[DatabaseCommunicator]: User with username '"
						+ username + "' was not found in the database.");
			}
			rs.close();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[DatabaseCommunicator]: Failed to read result from the found user in .findUser().");
			e.printStackTrace();
		}
		try {
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * A query method to reduce code redundancy.
	 * @param query The SQL-query to be sent to the server.
	 * @param st The Statement to on which to execute the query.
	 * @return The ResultSet from the processed query.
	 * @throws SQLException The exception from a failed query for error handling.
	 */
	private ResultSet doQuery(String query, Statement st) throws SQLException {
		ResultSet rs = null;
		try {
			rs = st.executeQuery(query);
		} catch (SQLException e) {
			throw e;
		}
		return rs;
	}

	/**
	 * An update method to reduce code redundancy.
	 * @param query The SQL-query containing INSERT, UPDATE or DELETE to be sent to the server.
	 * @param st The Statement to on which to execute the query.
	 * @return The number of rows affected by the update.
	 * @throws SQLException The exception from a failed query for error handling.
	 */
	private int doUpdate(String query, Statement st) throws SQLException {
		int affectedRows = 0;
		try {
			affectedRows = st.executeUpdate(query);
		} catch (SQLException e) {
			throw e;
		}
		return affectedRows;
	}

	/**
	 * Closes all connections and frees all resources used.
	 */
	private void terminate() {
		try {
			if(conn!=null) {
				conn.close();
			}
			instance = null;
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "[DatabaseCommunicator]: Failed to close the connection to the database.");
			e.printStackTrace();
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
			props.setProperty("password","FleetElite");
			//			props.setProperty("ssl","true");	//TODO When we change it to ssl.
			try {
				connection = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				Logger.getLogger("Debug").log(Level.SEVERE, "[DatabaseConnector]: Failed to establish a connection to the database.");
				e.printStackTrace();
			}
			return connection;
		}
	}

	//FIXME temporary main method to easily test the class.
	public static void main(String[] args) {
		DatabaseCommunicator dc = DatabaseCommunicator.getInstance();
		Scanner in = new Scanner(System.in);
		String input = "";
		while (!input.equals("q")) {
			input = in.nextLine();
			if (input.toLowerCase().startsWith("finduser")) {
				String[] params = input.split(" ");
				UserInfo result = dc.findUser(params[1]);
				System.out.println(result!=null?result:"Not found");
			} else if (input.toLowerCase().startsWith("adduser")) {
				String[] params = input.split(" ");
				String result = dc.addUser(params[1], params[2]);
				System.out.println(result!=null?result:"OK");
			} else if (input.toLowerCase().startsWith("deleteuser")) {
				String[] params = input.split(" ");
				String result = dc.deleteUser(params[1]);
				System.out.println(result!=null?result:"OK");
			}
		}
		in.close();
		dc.terminate();
	}
}
