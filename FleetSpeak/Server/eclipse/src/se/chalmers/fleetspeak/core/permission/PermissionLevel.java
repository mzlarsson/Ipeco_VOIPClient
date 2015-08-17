package se.chalmers.fleetspeak.core.permission;

import java.util.Arrays;
import java.util.List;

public enum PermissionLevel {
	ADMIN_ALL(),
	MODIFY_ROOM(PermissionType.MOVE_OTHER_USER, PermissionType.MOVE_OWN_USER, PermissionType.RENAME_OTHER_USER, PermissionType.RENAME_OWN_USER, PermissionType.RENAME_ROOM),
	MODIFY_ROOM_OWN_PROPS(PermissionType.MOVE_OWN_USER, PermissionType.RENAME_OWN_USER),
	DEFAULT(PermissionType.MOVE_OWN_USER, PermissionType.RENAME_OWN_USER);
	
	private List<PermissionType> permissions;
	
	private PermissionLevel(PermissionType... permissions){
		this.permissions = Arrays.asList(permissions);
	}
	
	public boolean accepts(PermissionType permission){
		return permissions.contains(permission) || this==ADMIN_ALL;
	}
	
	public boolean accepts(PermissionLevel level){
		for(PermissionType permission : this.permissions){
			if(!level.accepts(permission)){
				return false;
			}
		}
		
		return true;
	}
}
