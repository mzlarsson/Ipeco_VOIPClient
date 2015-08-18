package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.util.Command;

@FunctionalInterface
public interface BuildingManager {
	void handleCommand(Command c, int roomid);
}
