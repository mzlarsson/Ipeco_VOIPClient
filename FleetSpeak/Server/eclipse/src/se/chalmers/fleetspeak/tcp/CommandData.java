package se.chalmers.fleetspeak.tcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandData {

	private Map<String, Object> commands = new HashMap<String, Object>();
	private List<CommandListener> listeners = new ArrayList<CommandListener>();
	
	public void addCommandListener(CommandListener listener){
		listeners.add(listener);
	}
	
	public void removeCommandListener(CommandListener listener){
		listeners.remove(listener);
	}
	
	public void setValue(String key, Object value){
		for(int i = 0; i<listeners.size(); i++){
			listeners.get(i).commandChanged(key, getValue(key), value);
		}
		
		commands.put(key, value);
	}
	
	public boolean hasValue(String key){
		return commands.get(key)!=null;
	}
	
	public Object getValue(String key){
		return commands.get(key);
	}
}
