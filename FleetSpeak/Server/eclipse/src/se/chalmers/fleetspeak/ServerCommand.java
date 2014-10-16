package se.chalmers.fleetspeak;

import java.util.ArrayList;

public enum ServerCommand {
	
	CLEAR("cls") {
		@Override
		public String getInfo(){
			return "<b>cls</b> Clears the console window of all text.";
		}
	},
	CLOSE("close") {
		@Override
		public String getInfo(){
			return "<b>close</b> Shuts down the server.";
		}
	},
	SET_RTP_PORT("setRtpPort") {
		@Override
		public String getInfo(){
			return "<b>setRtpPort <user-id> <rtp-port></b> Sets the given RTP port for the user with the given ID.";
		}
	},
	HELP("help") {
		@Override
		public String getInfo(){
			return "<b>help <command></b> Gives help about the given command.";
		}
	},
	ROOM_INFO("roomInfo") {
		@Override
		public String getInfo(){
			return "<b>roomInfo</b> Shows all the current rooms with all users.";
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
