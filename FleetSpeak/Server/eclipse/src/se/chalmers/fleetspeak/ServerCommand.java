package se.chalmers.fleetspeak;

import java.util.ArrayList;

public enum ServerCommand {
	
	CLEAR("cls") {
		@Override
		public String getInfo(){
			return "\"cls\" Clears the console window of all text.";
		}
	},
	SET_RTP_PORT("setRtpPort") {
		@Override
		public String getInfo(){
			return "\"setRtpPort <user-id> <rtp-port>\" Sets the given RTP port for the user with the given ID.";
		}
	},
	HELP("help") {
		@Override
		public String getInfo(){
			return "\"help <command>\" Gives help about the given command.";
		}
	};
	
	private String cmd;
	
	private ServerCommand(String command) {
		this.cmd = command;
	}
	
	public String getName() {
		return cmd;
	}
	
	public String getInfo() {
		return "No information";
	}
	
	public static ServerCommand getCommand(String str) {
		for (ServerCommand cmd : ServerCommand.values()) {
			if (cmd.getName().equals(str)) {
				return cmd;
			}
		}
		return null;
	}
	
	public static ArrayList<ServerCommand> getPossibleCommands(String str) {
		ArrayList<ServerCommand> list = new ArrayList<ServerCommand>();
		for (ServerCommand cmd : ServerCommand.values()) {
			if (cmd.getName().startsWith(str)) {
				list.add(cmd);
			}
		}
		return list;
	}
}
