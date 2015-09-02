package se.chalmers.fleetspeak.gui;

import java.util.ArrayList;
import java.util.List;

public class CommandSearcher {

	private static String[] commandNames = {};							//FIXME fill with commands.
	private static List<String> result = new ArrayList<String>();
	private static int searchIndex = 0;
	
	private static String prevSearch = null;
	
	public static String search(String search){
		result.clear();
		for(String name : commandNames){
			if(name.toLowerCase().startsWith(search.toLowerCase())){
				result.add(name);
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
