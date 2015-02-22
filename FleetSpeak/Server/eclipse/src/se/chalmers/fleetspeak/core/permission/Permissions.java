package se.chalmers.fleetspeak.core.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Permissions {
	
	private static Map<Integer, List<PermissionLevel>> data = new HashMap<Integer, List<PermissionLevel>>();

	public static boolean isAllowed(int requester, PermissionLevel level){
		if(data.containsKey(requester)){
			for(PermissionLevel permLevel : data.get(requester)){
				if(permLevel.accepts(level)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean isAllowed(int requester, Permission permission){
		if(data.containsKey(requester)){
			for(PermissionLevel permLevel : data.get(requester)){
				if(permLevel.accepts(permission)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void addUserLevel(int id, PermissionLevel level){
		if(data.containsKey(id)){
			data.get(id).add(level);
		}else{
			List<PermissionLevel> list = new ArrayList<PermissionLevel>();
			list.add(level);
			data.put(id, list);
		}
	}
	
	public static void removeUserLevel(int id, PermissionLevel level){
		if(data.containsKey(id)){
			data.get(id).remove(level);
		}
	}
	
}
