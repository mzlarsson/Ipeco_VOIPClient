package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

	private static int DEFAULT_PORT_TCP = 8867;
	private static int DEFAULT_PORT_RTP = 8868;

	private int tcpPort;
	private int rtpPort;

	private static List<Client> clients = new ArrayList<Client>();
	private static ServerSocket serverSocket = null;
	
	private static Logger logger;

	private volatile boolean running;

	public static void main(String[] args) throws IOException {
		// Setup info about connection

		int tcpPort = (args != null && args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT_TCP);
		int rtpPort = (args != null && args.length>1?Integer.parseInt(args[1]):DEFAULT_PORT_RTP);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {

				for (int i = 0; i < clients.size(); i++) {
					clients.get(i).close();
				}
			}
		}));

		new ServerMain(tcpPort, rtpPort, false).start();
		;

	}

	public ServerMain(int tcpPort, int rtpPort, boolean globLogOn) {
		this.tcpPort = tcpPort;
		this.rtpPort = rtpPort;
		this.logger = globLogOn?Logger.getGlobal():null;
	}

	public void start() throws UnknownHostException {
		// Start the server
		log("Starting server @LAN-IP "
				+ InetAddress.getLocalHost().getHostAddress() + " tcp:"
				+ tcpPort + " rtp:" + rtpPort);
		this.running = true;
		try {
			serverSocket = new ServerSocket(tcpPort);
			Socket clientSocket = null;
			while (running) {
				// Create connection
				clientSocket = serverSocket.accept();
				log("Receiving connection request from client...");
				// Create client
				Client client = new Client(clientSocket, rtpPort,
						StringUtil.generateRandomCode(16));
				// Add to client list
				addClient(client);
			}

			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			log("[SERVER] " + e.getMessage());
			terminate();
		}
	}

	public static void addClient(Client client) {
		// Save the handler for more interaction
		clients.add(client);

		// Print info in server console
		log("A new person joined (" + clients.size() + ")");
	}

	public static void removeClient(Client client) {
		// Remove the handler from more interaction
		clients.remove(client);

		// Print info in server console
		log("A person left the room (" + clients.size() + ")");
	}

	private static void log(String msg) {
		if (logger!=null) {
			logger.log(Level.ALL, msg);
		} else {
			System.out.println(msg);
		}
	}

	public void terminate() {
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
		}

		running = false;
	}
}