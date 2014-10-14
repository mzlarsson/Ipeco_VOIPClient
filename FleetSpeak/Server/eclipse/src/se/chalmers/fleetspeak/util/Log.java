package se.chalmers.fleetspeak.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	private static Logger logger;
	private static Log instance;
	
	public static void log(String msg) {
		if (logger!=null) {
			logger.log(Level.ALL, msg);
		} else {
			System.out.println(msg);
		}
	}
	
	public static void setupLogger(Logger logger) {
		if (Log.logger==null) {
			if (instance==null) {
				instance = new Log();
			}
			instance.setLogger(logger);
		}
	}
	
	public void setLogger(Logger logger) {
		Log.logger = logger;
	}
}
