package se.chalmers.fleetspeak.core.command.impl;

import java.util.List;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.Room;
import se.chalmers.fleetspeak.core.RoomHandler;
import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.permission.Permission;
import se.chalmers.fleetspeak.core.permission.Permissions;


public class Cleanse extends BasicCommand{
	
	public Cleanse(int execCode){
		super(new CommandInfo("Cleanse", "Cleanse", "Kicks everyone from the server. Restores room.", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			if(Permissions.isAllowed(requester, Permission.KICK)){
				RoomHandler handler = RoomHandler.getInstance();
				Commands cmds = Commands.getInstance();
				CommandInfo info = cmds.findCommand("disconnect");
				for(Room r : handler.getRooms()){
					List<Client> clients = handler.getClients(r);
					for(int i = 0; i<clients.size(); i++){
						cmds.execute(requester, info, clients.get(i).getClientID());
					}
				}
				return new CommandResponse(true, "Successfully cleared server");
			}else{
				return new CommandResponse(false, "Insuffient permissions. Action denied.");
			}
		}catch(NumberFormatException | NullPointerException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
