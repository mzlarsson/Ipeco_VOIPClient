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
	HELP("help") {
		@Override
		public String getInfo(){
			return "<b>help <command></b> Gives help about the given command.";
		}
	},
	MOVE_USER("moveUser") {
		@Override
		public String getInfo(){
			return "<b>moveUser <user-id> <room-id></b> Moves user to given room.";
		}
	},
	MOVE_USER_NEW_ROOM("moveUserToNewRoom") {
		@Override
		public String getInfo(){
			return "<b>moveUser <user-id> <room-name></b> Moves user to a new room with the given name.";
		}
	},
	ROOM_INFO("roomInfo") {
		@Override
		public String getInfo(){
			return "<b>roomInfo</b> Shows all the current rooms with all users.";
		}
	},
	SET_RTP_PORT("setRtpPort") {
		@Override
		public String getInfo(){
			return "<b>setRtpPort <user-id> <rtp-port></b> Sets the given RTP port for the user with the given ID.";
		}
	},
	SET_SOUND_FORMAT("setSoundFormat"){
		@Override
		public String getInfo(){
			return "<b>setSoundFormat <sampleRate> <sampleSizeInBits> <channels> <frameSize> <frameRate> <bigEndian></b> Sets the sound format for all channels\n(default 8000.0F 8 1 1 8000.0F false)";
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
