package se.chalmers.fleetspeak.core.permission;

import java.util.Arrays;
import java.util.List;

public enum PermissionLevel {
	ADMIN_ALL(),
	MODIFY_ROOM(Permission.MOVE_OTHER_USER, Permission.MOVE_OWN_USER, Permission.RENAME_OTHER_USER, Permission.RENAME_OWN_USER, Permission.RENAME_ROOM),
	MODIFY_ROOM_OWN_PROPS(Permission.MOVE_OWN_USER, Permission.RENAME_OWN_USER);
	
	private List<Permission> permissions;
	
	private PermissionLevel(Permission... permissions){
		this.permissions = Arrays.asList(permissions);
	}
	
	public boolean accepts(Permission permission){
		return permissions.contains(permission) || this==ADMIN_ALL;
	}
	
	public boolean accepts(PermissionLevel level){
		for(Permission permission : this.permissions){
			if(!level.accepts(permission)){
				return false;
			}
		}
		
		return true;
	}
}
