package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.Client;
import se.chalmers.fleetspeak.core.RoomHandler;

public class SetRtpPort extends BasicCommand implements ICommand {

	public SetRtpPort(int execCode){
		super(new CommandInfo("SetRtpPort", "SetRtpPort [user-id] [rtp-port]", "Sets the given RTP port for the user with the given ID.", execCode));
	}
	
	@Override
	public boolean execute(int requester, Object... params) {
		try{
			int userID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
			int port = (params[1].getClass()==Integer.class||params[1].getClass()==int.class?(Integer)params[1]:Integer.parseInt((String)params[1]));
			Client c = RoomHandler.getInstance().findClient(userID);
			c.startRTPTransfer(port);
			return true;
		}catch(NumberFormatException nfe){}

		return false;
	}

}
