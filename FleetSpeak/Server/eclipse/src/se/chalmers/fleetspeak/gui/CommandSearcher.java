package se.chalmers.fleetspeak.gui;

import java.util.ArrayList;
import java.util.List;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.core.command.impl.CommandInfo;

public class CommandSearcher {

	private static CommandInfo[] commands;
	private static List<String> result = new ArrayList<String>();
	private static int searchIndex = 0;
	
	private static String prevSearch = null;
	
	public static String search(String search){
		if(commands == null){
			commands = Commands.getInstance().getCommands();
		}
		
		result.clear();
		for(CommandInfo info : commands){
			if(info.getName().toLowerCase().startsWith(search.toLowerCase())){
				result.add(info.getName());
			}
		}
		searchIndex = 0;
		prevSearch = search;
		
		return getValue();
	}
	
	public static String next(){
		if(result!=null && result.size()>0){
			searchIndex = (searchIndex+1)%result.size();
		}
		
		return getValue();
	}
	
	public static void clearSearch(){
		result.clear();
		prevSearch = null;
	}
	
	public static boolean hasSearch(){
		return !result.isEmpty();
	}
	
	private static String getValue(){
		if(searchIndex>=0 && searchIndex<result.size()){
			return result.get(searchIndex);
		}else{
			return prevSearch;
		}
	}
}
