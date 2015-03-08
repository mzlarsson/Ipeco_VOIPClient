package se.chalmers.fleetspeak.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class for handling the Logger class
 *
 */
public class Log {
	private static Logger logger;
	private static Log instance;
	
	/**
	 * Adds a string tyoe message to the logger
	 * @param msg The String to be transmitted to the logger
	 */
	public static void log(String msg) {
		if (logger!=null) {
			logger.log(Level.ALL, msg);
		} else {
			System.out.println(msg);
		}
	}
	
	/**
	 * Adds an error log message
	 * @param msg The String to be transmitted to the logger
	 */
	public static void logError(String msg) {
		Log.log("<error>" + msg + "</error>");
	}
	
	/**
	 * Adds an info log message
	 * @param msg The String to be transmitted to the logger
	 */
	public static void logInfo(String msg) {
		Log.log("<info>" + msg + "</info>");
	}
	
	/**
	 * Adds an debug log message
	 * @param msg The String to be transmitted to the logger
	 */
	public static void logDebug(String msg) {
		Log.log("<debug>" + msg + "</debug>");
	}
	
	/**
	 * Adds an exception log messsage
	 * @param ex The exception to be transmitted to the logger
	 */
	public static void logException(Exception ex) {
		String errorMsg = ex.toString()+"\n";
		StackTraceElement[] elements = ex.getStackTrace();
		StackTraceElement el = null;
		for(int i = elements.length-1; i>=0; i--){
			el = elements[i];
			errorMsg += "\t<i>"+el.getClassName()+"."+el.getMethodName()+"</i> line <b>"+el.getLineNumber()+"</b>\n";
		}
		Log.log(errorMsg);
	}
	
	/**
	 * Clears the log
	 */
	public static void flushLog() {
		if (logger!=null) {
			logger.getHandlers()[0].flush();
		}
	}
	
	/**
	 * If the logger is not initialized, sets a new one
	 * @param logger The logger to be used
	 */
	public static void setupLogger(Logger logger) {
		if (Log.logger==null) {
			if (instance==null) {
				instance = new Log();
			}
			instance.setLogger(logger);
		}
	}
	
	/**
	 * Sets the logger to be used
	 * @param logger The logger to be used
	 */
	public void setLogger(Logger logger) {
		Log.logger = logger;
	}
}
