package se.chalmers.fleetspeak.network.tcp;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.CommandHandler;

/**
 * For handling of TCP connections with the andriod app
 *
 * @author Nieo, Patrik Haar
 */

public class TCPHandler extends Thread{

	private static final int TIMEOUT_TIME = 15000;
	
	private Socket clientSocket;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;
	private boolean isRunning = false;
	private long lastContact;
	private CommandHandler ch;
	private Logger logger;

	/**
	 * Constructs the TCPHandler for a specific client.
	 * @param clientSocket The socket of the client.
	 * @param clientID The ID identifying the client.
	 */
	public TCPHandler(Socket clientSocket) {
		super("TCPHandler:port"+clientSocket.getRemoteSocketAddress());
		logger = Logger.getLogger("Debug");
		this.clientSocket = clientSocket;
		try {
			logger.log(Level.FINE,"Trying to get streams");
			printWriter = new PrintWriter(clientSocket.getOutputStream());
			bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			logger.log(Level.FINE,"Got streams");
		}
		catch (IOException e) {
			logger.log(Level.WARNING,e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * Looks for new incoming messages
	 */
	@Override
	public void run() {
		isRunning = true;
		String read = null;
		try {
			clientSocket.setSoTimeout(TIMEOUT_TIME); // Keep-alive messages is initiated if more than 15 seconds pass without contact.
			while (isRunning && bufferedReader != null) {
				logger.log(Level.FINER,"trying to read");

				try {
					read = bufferedReader.readLine();
				} catch(SocketTimeoutException e){
					long timeDiff = System.currentTimeMillis()-lastContact;
					if (timeDiff > 2*TIMEOUT_TIME) {
						throw e;
					} else if (timeDiff > TIMEOUT_TIME) {
						new Thread() {
							public void run() {
								try {
									sendCommand("ping"); // Sending ping in a new thread since it might block if something fails.
								} catch (IOException e) {}
							}
						}.start();
					}
					continue;
				}
				if(read != null){
					receivedCommand(read);
				}else{
					receivedCommand("{\"command\":\"disconnect\"}");
					isRunning = false;
				}

			}
		} catch(EOFException eofe){
			receivedCommand("{\"command\":\"disconnect\"}");
		} catch(SocketTimeoutException e){
			logger.log(Level.SEVERE, "Got Socket Timeout. Removing client");
			receivedCommand("{\"command\":\"disconnect\"}");
		} catch(SocketException e){
			//Only log if the handler is not terminated
			if(isRunning){
				logger.log(Level.SEVERE, e.getMessage());
			}
			receivedCommand("{\"command\":\"disconnect\"}");
		}catch (IOException e) {
			receivedCommand("{\"command\":\"disconnect\"}");
			logger.log(Level.SEVERE,e.getMessage());
		}finally{
			try {
				bufferedReader.close();
				printWriter.close();
				clientSocket.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}

		}
	}

	private void receivedCommand(String command) {
		lastContact = System.currentTimeMillis();
		if (!command.equals("pong")) { // TODO Ignores pong responses but there might not be a need for the client to respond at all, the server should fail to send the ping message if not reachable.
			if (ch != null) {
				logger.log(Level.FINE,  command);
				ch.handleCommand(command);
			} else {
				logger.log(Level.SEVERE, "Received a Command without a set CommandHandler");
			}
		}
	}

	/**
	 * Tries to send a command to the socket.
	 * @param command The Command to be sent.
	 * @throws IOException Throws exception if PrinterWriter failed to send a string.
	 */
	public void sendCommand(String command) throws IOException{
		logger.log(Level.FINER,command);
		printWriter.println(command);
		if(printWriter.checkError()){
			throw new IOException("PrinterWriter got an error");
		} else {
			lastContact = System.currentTimeMillis();
		}
	}

	/**
	 * Returns the IP of the connection.
	 * @return The IP of the connection.
	 */
	public InetAddress getInetAddress() {
		return clientSocket.getInetAddress();
	}

	/**
	 * Sets the CommandHandler which will handle the incoming Commands.
	 * @param ch The CommandHandler to be used.
	 */
	public void setCommandHandler(CommandHandler ch) {
		this.ch = ch;
	}
	
	/**
	 * Stops the TCPHandler
	 * @return If the clientSocket was successfully closed returns true, else false.
	 */
	public boolean terminate() {
		isRunning = false;
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
