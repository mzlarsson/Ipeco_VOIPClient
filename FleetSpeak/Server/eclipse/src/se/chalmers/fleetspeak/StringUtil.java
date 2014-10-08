package se.chalmers.fleetspeak;

import java.util.Random;

public class StringUtil {

	private static final String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static Random random = new Random();
	

    public static String generateRandomCode(int length){
    	StringBuilder s = new StringBuilder(length);
    	for(int i = 0; i <length; i++){
    		s.append(letters.charAt(random.nextInt(letters.length())));
    	}
    	
    	return s.toString();
    }
}
