package se.chalmers.fleetspeak.core;

import java.util.ArrayList;

public enum ServerCommand {
	
	CLEAR("cls") {
		@Override
		public String getInfo(){
			return "<b>cls</b> Clears the console window of all text.";
		}
	},
	HELP("help") {
		@Override
		public String getInfo(){
			return "<b>help [command]</b> Gives help about the given command.";
		}
	},
	KICK("kick") {
		@Override
		public String getInfo(){
			return "<b>kick [user-id]</b> Kicks the user with the given ID.";
		}
	},
	MOVE_USER("moveUser") {
		@Override
		public String getInfo(){
			return "<b>moveUser [user-id] [room-id]</b> Moves user to given room.";
		}
	},
	MOVE_USER_NEW_ROOM("moveUserToNewRoom") {
		@Override
		public String getInfo(){
			return "<b>moveUserToNewRoom [user-id] [room-name]</b> Moves user to a new room with the given name.";
		}
	},
	ROOM_INFO("roomInfo") {
		@Override
		public String getInfo(){
			return "<b>roomInfo</b> Shows all the current rooms with all users.";
		}
	},
	SET_ROOM_NAME("setRoomName") {
		@Override
		public String getInfo(){
			return "<b>setRoomName [room-id] [room-name]</b> Sets the name of the room with the given ID.";
		}
	},
	SET_RTP_PORT("setRtpPort") {
		@Override
		public String getInfo(){
			return "<b>setRtpPort [user-id] [rtp-port]</b> Sets the given RTP port for the user with the given ID.";
		}
	},
	SET_USER_NAME("setUsername") {
		@Override
		public String getInfo(){
			return "<b>setUsername [user-id] [user-name]</b> Sets the name of the user with the given ID.";
		}
	},;
	
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
