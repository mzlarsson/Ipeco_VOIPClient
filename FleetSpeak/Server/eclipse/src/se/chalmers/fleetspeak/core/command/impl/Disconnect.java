package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.util.Command;

public class Disconnect extends BasicCommand{
	
	public Disconnect(int execCode){
		super(new CommandInfo("Disconnect", "Disconnect [user-id]", "Removes the user from the server", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			int userID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
			if(RoomHandler.getInstance().removeClient(userID, true)){
				EventBus.postEvent("broadcast", new Command("userDisconnected", userID, null), null);
				return new CommandResponse(true, "The client has been disconnected from the server");
			}else{
				return new CommandResponse(false, "Internal error occured. Failed to perform action.");
			}
		}catch(NumberFormatException | NullPointerException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
