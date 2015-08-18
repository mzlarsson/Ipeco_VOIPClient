package se.chalmers.fleetspeak.core.permission;

public class BuildingPermission extends Permission {

	private int buildID;
	
	public BuildingPermission(PermissionType pt) {
		super(pt);
	}

	public int getBuildingID() {
		return buildID;
	}
}