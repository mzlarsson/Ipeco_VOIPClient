package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.command.CommandListener;


/**
 * Moves an existing user to an existing room.
 * Parameters:	1. The ID of the user to be moved.
 * 				2. The ID of the targeted room.
 * @author Patrik Haar
 *
 */
class MoveUser implements ICommand {

	private CommandInfo info;
	private CommandListener handler;
	
	/**
	 * Constructor for the command, creates the relevant
	 * info-object describing this command.
	 * @param exCode The execution-code used to locate this command.
	 */
	public MoveUser(int exCode) {
		info = new CommandInfo(	"MoveUser",
								"MoveUser [user-id] [target-room-id]",
								"Moves the user with the given user-ID to"
								+ "the room with the given room-ID",
								exCode);
	}
	
	@Override
	public boolean execute(int requester, Object... params) {
		if (handler == null) {
			return false;
		}
		int userID, roomID;
		userID = params[0].getClass() == Integer.class ?
				(Integer)params[0] : Integer.parseInt((String)params[0]);
		roomID = params[1].getClass() == Integer.class ?
				(Integer)params[1] : Integer.parseInt((String)params[1]);
		return handler.moveUser(requester, userID, roomID);
	}

	@Override
	public CommandInfo getInfo() {
		return info;
	}

	@Override
	public void setCommandListener(CommandListener cl) {
		handler = cl;
	}

}
