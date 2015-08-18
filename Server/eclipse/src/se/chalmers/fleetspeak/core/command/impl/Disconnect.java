package se.chalmers.fleetspeak.core.command.impl;


public class Disconnect extends BasicCommand{
	
	public Disconnect(int execCode){
		super(new CommandInfo("Disconnect", "Disconnect [user-id]", "Removes the user from the server", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		throw new IllegalStateException("Command disconnect is broken.");
		
//		try{
//			int userID = (params[0].getClass()==Integer.class||params[0].getClass()==int.class?(Integer)params[0]:Integer.parseInt((String)params[0]));
//			if(RoomHandler.getInstance().removeClient(userID, true)){
//				return new CommandResponse(true, "The client has been disconnected from the server");
//			}else{
//				return new CommandResponse(false, "Internal error occured. Failed to perform action.");
//			}
//		}catch(NumberFormatException | NullPointerException | IndexOutOfBoundsException e){
//			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
//		}
	}

}