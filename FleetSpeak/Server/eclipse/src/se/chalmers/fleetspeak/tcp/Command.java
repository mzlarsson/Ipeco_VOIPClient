package se.chalmers.fleetspeak.tcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command {

	public static Map<String, Object> commands = new HashMap<String, Object>();
	public static List<CommandListener> listeners = new ArrayList<CommandListener>();
	
	private Command(){}
	
	public static void addCommandListener(CommandListener listener){
		listeners.add(listener);
	}
	
	public static void removeCommandListener(CommandListener listener){
		listeners.remove(listener);
	}
	
	public static void setValue(String key, Object value){
		commands.put(key, value);

		for(int i = 0; i<listeners.size(); i++){
			listeners.get(i).commandChanged(key, value);
		}
	}
	
	public static boolean hasValue(String key){
		return commands.get(key)!=null;
	}
	
	public static Object getValue(String key){
		return commands.get(key);
	}
}
