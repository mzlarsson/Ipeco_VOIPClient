package se.chalmers.fleetspeak.test;

import java.util.HashMap;
import java.util.Map;

public class MapFunctionalityTester {

	public static void main(String[] args){
		Map<Integer, String> tmp = new HashMap<Integer, String>();
		tmp.put(1, "Ett");
		tmp.put(2, "Två");
		tmp.put(3, "Tre");
		for(Integer i : tmp.keySet()){
			tmp.put(i, "poop");
		}
		System.out.println("no fucking error");
	}
	
}
