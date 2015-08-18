package se.chalmers.fleetspeak.core.command.impl;


public class AddUser extends BasicCommand{

	public AddUser(int execCode){
		super(new CommandInfo("AddUser", "AddUser [Client]", "Adds a client to the model. Warning: This is only an internal commando", execCode));
	}
	
	@Override
	public CommandResponse execute(int requester, Object... params){
		throw new IllegalStateException("Command addUser is broken");
		
//		if(params.length<2){
//			return new CommandResponse(false, "Insufficient number of parameters");
//		}
//		if(!(params[0] instanceof Client)){
//			return new CommandResponse(false, "Parameter 1 in AddUser must be of type Client");
//		}
//		if(!(params[1] instanceof PermissionLevel)){
//			return new CommandResponse(false, "Parameter 2 in AddUser must be of type PermissionLevel");
//		}
//		
//		Client client = (Client)params[0];
//		PermissionLevel permLevel = (PermissionLevel)params[1];
//		Permissions.addUserLevel(client.getClientID(), permLevel);
//		RoomHandler.getInstance().addClient(client);
//		return new CommandResponse(true, "The client was added to the server");
	}

}
