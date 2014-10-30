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
	private Logger log;
	
	public static void main(String[] args) {
		int port = (args!=null&&args.length>0?Integer.parseInt(args[0]):DEFAULT_PORT);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
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
			}
		}));
        
        new ServerConsole(port);
	}

	public ServerConsole(final int port) {
		
		setupLogger();
		
		serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					server = new ConnectionHandler(port);
					server.start();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
	
	private void setupLogger() {
		log = Logger.getGlobal();
		log.setLevel(Level.ALL);
		Handler logHandler = new Handler() {
			
			@Override
			public void publish(LogRecord record) {
				String msg = record.getMessage();
				if (msg.indexOf("<")==-1) {
					System.out.println(msg);
				} else {
					StringBuilder str = new StringBuilder("");
					int tagStart = msg.indexOf("<");
					int tagEnd = msg.indexOf(">", tagStart);
					while (tagStart!=-1) {
						str.append(msg.substring(0, tagStart));
						if (tagEnd!=-1) {
							if (!setAttributes(msg.substring(tagStart+1, tagEnd))) {
								str.append(msg.substring(tagStart, tagEnd+1));
							}
							msg = msg.substring(tagEnd+1);
						} else {
							str.append("<");
							msg = msg.substring(1);
						}
						tagStart = msg.indexOf("<");
						tagEnd = msg.indexOf(">", tagStart);
					}
					str.append(msg);
					System.out.println(str.toString());
				}
			}
			
			// Returns true if the given string is an actual attribute.
			private boolean setAttributes(String msg) {
				String[] markups = {"b", "i", "error", "info", "debug"};
				for (String markup : markups) {
					if (msg.equals(markup) || msg.equals("/"+markup)) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			public void flush() {
				// Clears the console.
				try {
					Runtime.getRuntime().exec("cls");
				} catch (IOException e) {
					Log.log("Could not empty log");
				}
			}
			
			@Override
			public void close() throws SecurityException {
				// TODO Auto-generated method stub
				
			}
		};
		log.addHandler(logHandler);
		Log.setupLogger(log);
	}

}
