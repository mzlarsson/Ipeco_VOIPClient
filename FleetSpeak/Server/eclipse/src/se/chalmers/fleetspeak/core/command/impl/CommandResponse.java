package se.chalmers.fleetspeak.core.command.impl;

public class CommandResponse {

	private boolean success;
	private String message;
	private Object[] data;
	
	public CommandResponse(boolean success, String message, Object... data){
		this.success = success;
		this.message = message;
		this.data = data;
	}
	
	public String getMessage(){
		return message;
	}
	
	public boolean wasSuccessful(){
		return success;
	}
	
	public Object[] getData(){
		return data;
	}
}
