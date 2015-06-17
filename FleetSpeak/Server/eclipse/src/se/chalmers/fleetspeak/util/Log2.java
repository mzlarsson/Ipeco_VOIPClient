package se.chalmers.fleetspeak.util;

import java.io.IOException;
import java.util.logging.*;

/**
 * Created by Nieo on 17/06/15.
 */
public class Log2 {

    private static FileHandler fileHandler;
    private static Logger logger;
    private static final String LOG_NAME = "fleetspeak.log";



    public static void log(Level level, String msg){
        if(logger == null || fileHandler == null){
            logger= Logger.getLogger("Debug");
            try {
                fileHandler = new FileHandler(LOG_NAME, false);
                logger.addHandler(fileHandler);
                fileHandler.setFormatter(new myFormat());
            logger.setLevel(Level.ALL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.log(level , msg);
        fileHandler.flush();
    }

    private static class myFormat extends Formatter{

        @Override
        public String format(LogRecord record) {
            return record.getLevel() + ": " + record.getMessage() + "\n";
        }
    }




}
