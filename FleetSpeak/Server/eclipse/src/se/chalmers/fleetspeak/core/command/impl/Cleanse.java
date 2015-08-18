package se.chalmers.fleetspeak.core.command.impl;



public class Cleanse extends BasicCommand{
	
	public Cleanse(int execCode){
		super(new CommandInfo("Cleanse", "Cleanse", "Kicks everyone from the server. Restores room.", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		throw new IllegalStateException("Command cleanse is broken.");
		
//		try{
//			if(Permissions.isAllowed(requester, PermissionType.KICK)){
//				RoomHandler handler = RoomHandler.getInstance();
//				Commands cmds = Commands.getInstance();
//				CommandInfo info = cmds.findCommand("disconnect");
//				for(Room r : handler.getRooms()){
//					List<Client> clients = handler.getClients(r);
//					for(int i = 0; i<clients.size(); i++){
//						cmds.execute(requester, info, clients.get(i).getClientID());
//					}
//				}
//				return new CommandResponse(true, "Successfully cleared server");
//			}else{
//				return new CommandResponse(false, "Insuffient permissions. Action denied.");
//			}
//		}catch(NumberFormatException | NullPointerException e){
//			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
//		}
	}

}
