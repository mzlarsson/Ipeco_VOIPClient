package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;

public class Disconnect extends BasicCommand implements ICommand {
	
	public Disconnect(int execCode){
		super(new CommandInfo("Disconnect", "Disconnect [user-id]", "Removes the user from the server", execCode));
	}

	@Override
	public boolean execute(int requester, Object... params) {
		try{
			int userID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
			RoomHandler.getInstance().removeClient(userID, true);
			EventBus.getInstance().fireEvent(new EventBusEvent("broadcast", new Command("userDisconnected", userID, null), null));
			return true;
		}catch(NumberFormatException nfe){}
		
		return false;
	}

}
