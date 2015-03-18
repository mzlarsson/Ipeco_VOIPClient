package se.chalmers.fleetspeak.core.command.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import se.chalmers.fleetspeak.test.TCPModifierBot;

public class RunTestBot extends BasicCommand{
	
	public RunTestBot(int execCode){
		super(new CommandInfo("RunTestBot", "RunTestBot [name] [serverPort]", "Runs a test bot that performs some room changes etc.", execCode));
	}

	@Override
	public CommandResponse execute(int requester, Object... params){
		try{
			String name = (params[0].getClass()==String.class?(String)params[0]:"UnnamedBot");
			int port = (params[1].getClass()==Integer.class||params[1].getClass()==int.class?(Integer)params[1]:Integer.parseInt((String)params[1]));
			
			String ip;
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
				TCPModifierBot bot = new TCPModifierBot(name, ip, port);
				bot.start();
				return new CommandResponse(true, "Successfully started the bot");
			} catch (UnknownHostException e) {
				return new CommandResponse(false, "Failed to retrieve the local IP");
			}
		}catch(NumberFormatException | NullPointerException | IndexOutOfBoundsException e){
			return new CommandResponse(false, "Invalid command use: '"+getInfo().getFormat()+"'");
		}
	}

}
