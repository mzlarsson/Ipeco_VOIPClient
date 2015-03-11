package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.RoomHandler;

public class SetSoundPort extends BasicCommand {

	public SetSoundPort(int exCode) {
		super(new CommandInfo("SetSoundPort", "SetSoundPort [userID] [remoteUserID],[remotePort]",
							  "Sets the inport from a client to another client", exCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params) {
		try{
			int userID = params[0].getClass()==Integer.class ? (Integer)params[0] : Integer.parseInt((String)params[0]);
			String[] remoteData = ((String)params[1]).split(",");
			int remoteUserID = Integer.parseInt(remoteData[0]);
			int remoteUserPort = Integer.parseInt(remoteData[1]);
			
			RoomHandler handler = RoomHandler.getInstance();
			Client current = handler.findClient(userID);
			Client remote = handler.findClient(remoteUserID);
			if(current != null && remote != null){
				remote.addListeningClient(current, remoteUserPort);
				return new CommandResponse(true, current.getName()+" added "+remote.getName()+" on port "+remoteUserPort);
			}else{
				return new CommandResponse(false, "Internal error occured. Failed to perform action.");
			}
		}catch(NumberFormatException | NullPointerException | ArrayIndexOutOfBoundsException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
