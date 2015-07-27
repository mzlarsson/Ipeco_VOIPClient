package se.chalmers.fleetspeak.core_v2;

import se.chalmers.fleetspeak.util.Command;

@FunctionalInterface
public interface BuildingManager {
	void handleCommand(Command c, int roomid);
}
