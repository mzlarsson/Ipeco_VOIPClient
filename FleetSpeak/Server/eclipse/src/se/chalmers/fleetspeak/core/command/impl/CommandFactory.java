package se.chalmers.fleetspeak.core.command.impl;


public class CommandFactory {

	public static ICommand[] createCommands() {
		ICommand[] array = {new MoveUser(0)};
		
		return array;
	}
}
