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
	
	public static void logError(String msg) {
		Log.log("<error>" + msg + "</error>");
	}
	
	public static void logInfo(String msg) {
		Log.log("<info>" + msg + "</info>");
	}
	
	public static void logDebug(String msg) {
		Log.log("<debug>" + msg + "</debug>");
	}

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
	
	public static void flushLog() {
		if (logger!=null) {
			logger.getHandlers()[0].flush();
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
