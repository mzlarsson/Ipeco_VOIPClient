package se.chalmers.fleetspeak.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by Nieo on 17/06/15.
 */
public class Log2 {
	
	private static Log2 log = null;

	private FileHandler fileHandler;
	private Logger logger;

	/**
	 * Creates a new filehandler and add it to the Logger 'Debug'
	 * Add this to VM arguments in Run config to get everything on a single line
	 *	-Djava.util.logging.SimpleFormatter.format='%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n'
	 */

	private Log2(){
		System.out.println("starting logging");
		logger= Logger.getLogger("Debug");
		logger.setLevel(Level.ALL);


		try {
			FileInputStream configFile = new FileInputStream("log/logging.properties");

			LogManager.getLogManager().readConfiguration(configFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("logging strted");
	};
	
	/**
	 * Start the log handler
	 */
	public static void start(){
		//Start if not started.
		if(log == null){
			log = new Log2();
		}
	}
	
	/**
	 * Stops the handler
	 */
	public static void stop(){
		//Stop if started
		if(log != null){
			log.close();
			log = null;
		}
	}

	/**
	 * Closes logfile
	 * Needs to be called before shutdown
	 */
	private void close(){
		fileHandler.flush();
		fileHandler.close();
	}




}
