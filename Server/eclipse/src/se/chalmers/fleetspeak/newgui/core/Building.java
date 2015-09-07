package se.chalmers.fleetspeak.newgui.core;

import se.chalmers.fleetspeak.newgui.connection.CommandHandler;
import se.chalmers.fleetspeak.util.Command;

public class Building implements CommandHandler{
	
	private static Building building;
	
	private Building(String username, int id, int roomID){
		System.out.println("Starting Building for "+username+" [ID "+id+"]");
	}

	@Override
	public void commandReceived(Command cmd) {
		System.out.println("Building got command: "+cmd);
	}
	
	
	public static Building getInstance(String username, int id, int roomID){
		if(building == null){
			building = new Building(username, id, roomID);
		}
		
		return building;
	}

	public static void terminate(){
		if(building != null){
			building = null;
		}
	}
}
