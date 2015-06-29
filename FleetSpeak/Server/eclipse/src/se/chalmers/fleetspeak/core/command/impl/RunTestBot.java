package se.chalmers.fleetspeak.core.command.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.chalmers.fleetspeak.test.MusicBot;
import se.chalmers.fleetspeak.test.FileBot;

public class RunTestBot extends BasicCommand{
	
	public RunTestBot(int execCode){
		super(new CommandInfo("RunTestBot", "RunTestBot [name] [serverPort]", "Runs a test bot that performs some room changes etc.", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			String name = (params[0].getClass()==String.class?(String)params[0]:"UnnamedBot");
			int port = (params[1].getClass()==Integer.class||params[1].getClass()==int.class?(Integer)params[1]:Integer.parseInt((String)params[1]));
			Mode mode = Mode.FILEBOT;
			if(params.length>2 && params[2] instanceof String){
				try{
					mode = Mode.valueOf(((String)params[2]).toUpperCase());
				}catch(IllegalArgumentException e){}
			}
			
			String ip;
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
				Thread bot = null;
				switch(mode){
					case MUSICBOT:	bot = new MusicBot(name, ip, port);break;
					case FILEBOT:
					default:		bot = new FileBot(name, ip, port);break;
				}
				bot.start();
				return new CommandResponse(true, "Successfully started the bot");
			} catch (UnknownHostException e) {
				return new CommandResponse(false, "Failed to retrieve the local IP");
			}
		}catch(NumberFormatException | NullPointerException | IndexOutOfBoundsException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}
	
	
	public enum Mode{
		FILEBOT, MUSICBOT
	}

}
