package se.chalmers.fleetspeak.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * A singleton that handles communication with the database.
 *
 * @author Patrik Haar
 */
public class DatabaseCommunicator {

	private Connection conn;
	private static DatabaseCommunicator instance;
	
	private DatabaseCommunicator(){
		conn = DatabaseConnector.initiateConnection();
	}
	
	public static DatabaseCommunicator getInstance() {
		if (instance == null) {
			instance = new DatabaseCommunicator();
		}
		return instance;
	}
	
	/**
	 * Finds the information of the user from the database.
	 * @param username The unique username of the user.
	 * @return A UserInfo object with the information of the user if found, null if not found.
	 */
	public UserInfo findUser(String username) {
		if (conn == null) {
			throw new NullPointerException("[DatabaseCommunicator]: No connection to the database "
					+ "has been established");
		}
		UserInfo user = null;
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM users "
					+ "WHERE username = '"+username+"'");
			if (rs.next()) {
				user = new UserInfo(rs.getInt(1), rs.getString(2), rs.getString(3));
			}
			else {
				Log2.log(Level.INFO, ("[DatabaseCommunicator]: User with username \""
						+ username + "\" was not found in the database."));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	
	private void terminate() {
		try {
			if(conn!=null) {
				conn.close();
			}
		} catch (SQLException e) {
			Log2.log(Level.SEVERE, "[DatabaseCommunicator]: Failed to close the connection to the database.");
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
			props.setProperty("password","FleetElit");
//			props.setProperty("ssl","true");	//TODO When we change it to ssl.
			try {
				connection = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				Log2.log(Level.SEVERE, "[DatabaseConnector]: Failed to establish a connection to the database.");
				e.printStackTrace();
			}
			return connection;
		}
	}
}
