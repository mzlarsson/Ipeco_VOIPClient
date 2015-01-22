package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.InvalidCommandArgumentsException;
import se.chalmers.fleetspeak.eventbus.EventBus;
import se.chalmers.fleetspeak.eventbus.EventBusEvent;
import se.chalmers.fleetspeak.util.Command;

public class AddUser extends BasicCommand{

	public AddUser(int execCode){
		super(new CommandInfo("AddUser", "AddUser [Client]", "Adds a client to the model. Warning: This is only an internal commando", execCode));
	}
	
	@Override
	public boolean execute(int requester, Object... params) throws InvalidCommandArgumentsException{
		if(!(params[0] instanceof Client)){
			throw new InvalidCommandArgumentsException("Parameter 1 in AddUser must be of type Client");
		}
		
		Client client = (Client)params[0];
		RoomHandler.getInstance().addClient(client);
		EventBus.getInstance().fireEvent(new EventBusEvent("broadcast", new Command("newUser", client.getClientID(),null), null));
		return true;
	}

}
