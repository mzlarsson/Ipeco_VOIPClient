package se.chalmers.fleetspeak.core.command;

import se.chalmers.fleetspeak.core.command.impl.CommandInfo;

public class InvalidCommandArgumentsException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidCommandArgumentsException(){
		super("Invalid parameters was used for the command");
	}
	
	public InvalidCommandArgumentsException(CommandInfo info){
		super("Invalid command use: '"+info.getFormat()+"'");
	}
	
	public InvalidCommandArgumentsException(String msg){
		super(msg);
	}

}
