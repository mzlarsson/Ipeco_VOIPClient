package se.chalmers.fleetspeak.core.command.impl;

public abstract class BasicCommand implements ICommand {

	private CommandInfo info;
	
	public BasicCommand(CommandInfo info){
		this.info = info;
	}

	@Override
	public CommandInfo getInfo() {
		return this.info;
	}
	
	@Override
	public String toString(){
		return "[execCode="+info.getExecCode()+"] "+info.getName();
	}

}