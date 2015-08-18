package se.chalmers.fleetspeak;

import java.util.Scanner;

import se.chalmers.fleetspeak.core.MainController;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

public class ServerConsole {

	private static int DEFAULT_PORT = 8867;

	private static MainController server;
	private static Scanner scanner;

	public static void main(String[] args) {
		Log.start();
		int port = args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT;

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (server != null) {
				server.terminate();
				server = null;
				scanner.close();
				Log.stop();
			}
		}));

		new ServerConsole(port);
	}

	public ServerConsole(final int port) {

		server = new MainController(port);

		scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String input = scanner.nextLine();
			EventBus.getInstance().fireEvent(new EventBusEvent("CommandHandler", new Command("consoleCommand", null, input), this));
		}

	}



}
