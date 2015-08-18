package se.chalmers.fleetspeak.core.permission;

/**
 * Abstract class for the different types of permissions.
 *
 * @author Patrik Haar
 */
public abstract class Permission {

	private PermissionType pt;
	
	/**
	 * Constructor for the abstract class Permission.
	 * @param pt The PermissionType connected to this Permission.
	 */
	public Permission(PermissionType pt) {
		this.pt = pt;
	}
	
	/**
	 * The PermissionType connected to this Permission.
	 * @return The PermissionType connected to this Permission.
	 */
	public PermissionType getPermissionType() {
		return pt;
	}
}
