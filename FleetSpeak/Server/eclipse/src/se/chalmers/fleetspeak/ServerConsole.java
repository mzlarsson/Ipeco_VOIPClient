package se.chalmers.fleetspeak;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.core.ConnectionHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;
import se.chalmers.fleetspeak.util.Log;

public class ServerConsole {

    private static int DEFAULT_PORT = 8867;
    
    private static Thread serverThread;
    private static ConnectionHandler server;
    
    private static Scanner scanner;

	public static void main(String[] args) {
		int port = (args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                try {
                    server.terminate();
                    serverThread.join();
                    server = null;
                    scanner.close();
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }));
        
        new ServerConsole(port);
	}

	public ServerConsole(final int port) {
		
		serverThread = new Thread(() -> {
            try {
                server = new ConnectionHandler(port);
                server.start();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
		serverThread.start();
		
		scanner = new Scanner(System.in);
		while(serverThread.isAlive()) {
			while (scanner.hasNextLine()) {
				String input = scanner.nextLine();
				EventBus.getInstance().fireEvent(new EventBusEvent("CommandHandler", new Command("consoleCommand", null, input), this));
			}
		}
	}



}
