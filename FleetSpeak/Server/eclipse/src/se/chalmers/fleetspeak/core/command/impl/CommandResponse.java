package se.chalmers.fleetspeak.core.command.impl;

public class CommandResponse {

	private boolean success;
	private String message;
	
	public CommandResponse(boolean success, String message){
		this.success = success;
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
	
	public boolean wasSuccessful(){
		return success;
	}
	
}
