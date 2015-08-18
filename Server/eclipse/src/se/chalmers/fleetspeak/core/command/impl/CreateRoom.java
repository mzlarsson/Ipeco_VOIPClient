package se.chalmers.fleetspeak.core.command.impl;


public class CreateRoom extends BasicCommand {

	public CreateRoom(int exCode) {
		super(new CommandInfo("CreateRoom", "CreateRoom [name] [permanent (optional)]", "Creates a new room with given name", exCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params) {
		throw new IllegalStateException("Command createRoom is broken.");
		
//		try{
//			String name = (String)params[0];
//			boolean permanent = (params.length>1 && ((params[1] instanceof Boolean && (Boolean)params[1]) || (params[1] instanceof String && ((String)params[1]).equals("true"))));
//			if(Permissions.isAllowed(requester, PermissionType.RENAME_ROOM)){
//				Room room = new Room(name, permanent);
//				if(RoomHandler.getInstance().addRoom(room, requester != -1)){
//					return new CommandResponse(true, "Added room '"+name+"'", new Object[]{room.getId()});
//				}else{
//					return new CommandResponse(false, "Internal error occured. Failed to perform action.");
//				}
//			}else{
//				return new CommandResponse(false, "Insuffient permissions. Action denied.");
//			}
//		}catch(NumberFormatException | NullPointerException | ClassCastException | IndexOutOfBoundsException e){
//			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
//		}
	}

}
