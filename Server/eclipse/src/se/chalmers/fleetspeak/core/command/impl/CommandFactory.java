package se.chalmers.fleetspeak.core.command.impl;

public class CommandFactory {
	
	private static ICommand[] commands;
	
	public static ICommand[] createCommands(){
		if(commands == null){
			commands = new ICommand[12];
			commands[0] = new AddUser(0);
			commands[1] = new Cleanse(1);
			commands[2] = new CreateRoom(2);
			commands[3] = new DebugRooms(3);
			commands[4] = new Disconnect(4);
			commands[5] = new Help(5);
			commands[6] = new Kick(6);
			commands[7] = new MoveUser(7);
			commands[8] = new RunTestBot(8);
			commands[9] = new SetRoomName(9);
			commands[10] = new SetSoundPort(10);
			commands[11] = new SetUsername(11);
		}
		
		return commands;
	}
}
