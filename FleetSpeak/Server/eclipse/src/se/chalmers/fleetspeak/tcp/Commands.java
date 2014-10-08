package se.chalmers.fleetspeak.tcp;

public enum Commands {
	DISCONNECT("/disconnect"), 
	SET_NAME("/nick"),
	MUTE("/mute"),
	UNMUTE("/unmute");
	
	
	private String name;
	
	private Commands(String s){
		name = s;
	}
	public String getName(){
		return name;
	}
}
	