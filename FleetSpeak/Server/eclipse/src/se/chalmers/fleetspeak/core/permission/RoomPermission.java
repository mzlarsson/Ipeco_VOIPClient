package se.chalmers.fleetspeak.core.permission;

public class RoomPermission extends Permission {

	private int roomID;
	
	public RoomPermission(PermissionType pt) {
		super(pt);
	}

	public int getRoomID() {
		return roomID;
	}
}