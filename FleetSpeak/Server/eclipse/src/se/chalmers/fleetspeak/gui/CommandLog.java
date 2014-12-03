package se.chalmers.fleetspeak.gui;

import java.util.ArrayList;
import java.util.List;

public class CommandLog {

	private List<String> commands;
	private int counter = 0;
	private boolean firstFetch = false;
	
	public CommandLog(){
		commands = new ArrayList<String>();
	}
	
	public void add(String cmd){
		commands.add(cmd);
		counter = commands.size()-1;
		firstFetch = true;
	}
	
	public String getPreviousCommand(){
		if(commands.size()>0 && !firstFetch){
			counter = (counter-1+commands.size())%commands.size();
		}else{
			counter = Math.max(0, commands.size()-1);
			firstFetch = false;
		}
		
		return getCommand();
	}
	
	public String getNextCommand(){
		if(commands.size()>0){
			counter = (counter+1)%commands.size();
		}else{
			counter = Math.max(0, commands.size()-1);
			firstFetch = false;
		}
		
		return getCommand();
	}
	
	private String getCommand(){
		if(counter>=0 && counter<commands.size()){
			return commands.get(counter);
		}else{
			return null;
		}
	}
}