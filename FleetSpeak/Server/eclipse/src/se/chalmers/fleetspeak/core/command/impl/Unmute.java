package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.InvalidCommandArgumentsException;

public class Unmute extends BasicCommand{

	public Unmute(int execCode){
		super(new CommandInfo("Unmute", "Unmute [unmuterID] [unmutedID]", "Makes a user unmuted to another user", execCode));
	}
	
	@Override
	public boolean execute(int requester, Object... params) throws InvalidCommandArgumentsException{
		try{
			int muterID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
			int mutedID = (params[1].getClass()==Integer.class||params[1].getClass()==int.class?(Integer)params[1]:Integer.parseInt((String)params[1]));
			
			Client muter = RoomHandler.getInstance().findClient(muterID);
			Client muteObject = RoomHandler.getInstance().findClient(mutedID);
			if(muter != null && muteObject != null){
				muter.setMuted(muteObject, false);
				return true;
			}else{
				throw new InvalidCommandArgumentsException("Could not find requested client [Invalid clientID]");
			}
		}catch(NumberFormatException nfe){
			throw new InvalidCommandArgumentsException(getInfo());
		}catch(NullPointerException nfe){
			throw new InvalidCommandArgumentsException(getInfo());
		}
	}

}
