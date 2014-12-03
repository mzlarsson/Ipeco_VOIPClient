package se.chalmers.fleetspeak.gui;

import java.util.List;

import se.chalmers.fleetspeak.core.ServerCommand;

public class CommandSearcher {

	private static List<ServerCommand> result = null;
	private static int searchIndex = 0;
	
	private static String prevSearch = null;
	
	public static String search(String search){
		result = ServerCommand.getPossibleCommands(search);
		searchIndex = 0;
		prevSearch = search;
		
		return getValue();
	}
	
	public static String next(){
		searchIndex = (searchIndex+1)%result.size();
		
		return getValue();
	}
	
	public static void clearSearch(){
		result = null;
		prevSearch = null;
	}
	
	public static boolean hasSearch(){
		return result != null;
	}
	
	private static String getValue(){
		if(searchIndex>=0 && searchIndex<result.size()){
			return result.get(searchIndex).getName();
		}else{
			return prevSearch;
		}
	}
}
