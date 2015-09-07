package se.chalmers.fleetspeak.newgui.core;

import se.chalmers.fleetspeak.newgui.connection.CommandHandler;

public class Building implements CommandHandler{
	
	private static Building building;
	
	private Building(int id){
		System.out.println("Starting Building for [ID "+id+"]");
	}

	@Override
	public void commandReceived(String json) {
		System.out.println("Building got command: "+json);
	}
	
	
	public static Building getInstance(int id){
		if(building == null){
			building = new Building(id);
		}
		
		return building;
	}

	public static void terminate(){
		if(building != null){
			building = null;
		}
	}
}
