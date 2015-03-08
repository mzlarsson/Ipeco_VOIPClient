package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;


public class Kick extends BasicCommand{
	
	public Kick(int execCode){
		super(new CommandInfo("Kick", "Kick [user-id]", "Kicks the user with the given ID.", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			if(Permissions.isAllowed(requester, Permission.KICK)){
				return Commands.getInstance().execute(requester, Commands.getInstance().findCommand("Disconnect"), userID);
			}else{
				return new CommandResponse(false, "Insuffient permissions. Action denied.");
			}
		}catch(NumberFormatException | NullPointerException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
