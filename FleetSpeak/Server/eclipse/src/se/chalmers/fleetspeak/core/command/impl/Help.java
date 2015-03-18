package se.chalmers.fleetspeak.core.command.impl;

import se.chalmers.fleetspeak.core.command.Commands;
import se.chalmers.fleetspeak.util.Log;

public class Help extends BasicCommand{
	
	public Help(int execCode){
		super(new CommandInfo("Help", "Help | Help [name]", "Shows help about all/a certain command", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		if(params.length > 0){
			String cmd = (String)params[0];
			CommandInfo info = Commands.getInstance().findCommand(cmd);
			if(info != null){
				Log.log("\t"+info.getFormat()+"\n\t\t"+info.getDescription());
				return new CommandResponse(true, "The help information has been printed");
			}else{
				if(cmd.equalsIgnoreCase("Volt")){
					return new CommandResponse(false, "Unable to help Volt. Hopeless case...");
				}else{
					return new CommandResponse(false, "Unable to find command");
				}
			}
		}else{
			CommandInfo[] cmds = Commands.getInstance().getCommands();
			for(CommandInfo info : cmds){
				Log.log("\t"+info.getFormat()+"\n\t\t"+info.getDescription());
			}
			return new CommandResponse(true, "The help information has been printed");
		}
	}

}
