package se.chalmers.fleetspeak;

import se.chalmers.fleetspeak.core.MainController;
import se.chalmers.fleetspeak.util.Log;

public class ServerConsole {

	private static int DEFAULT_PORT = 8867;

	private static MainController server;

	public static void main(String[] args) {
		Log.start();
		int port = args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT;

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (server != null) {
				server.terminate();
				server = null;
				Log.stop();
			}
		}));

		new ServerConsole(port);
	}

	public ServerConsole(int port) {

		server = new MainController(port);


	}



}
