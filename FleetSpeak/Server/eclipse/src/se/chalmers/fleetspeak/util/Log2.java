package se.chalmers.fleetspeak.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Nieo on 17/06/15.
 */
public class Log2 {

	private FileHandler fileHandler;
	private Logger logger;
	private final String LOG_NAME = "fleetspeak.log";

	/**
	 * Creates a new filehandler and add it to the Logger 'Debug'
	 * Add this to VM arguments in Run config to get everything on a single line
	 *	-Djava.util.logging.SimpleFormatter.format='%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n'
	 */

	public Log2(){
		logger= Logger.getLogger("Debug");
		logger.setLevel(Level.ALL);
		try {
			fileHandler = new FileHandler(LOG_NAME);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

	/**
	 * Closes logfile
	 * Needs to be called before shutdown
	 */
	public void close(){
		fileHandler.flush();
		fileHandler.close();
	}




}
