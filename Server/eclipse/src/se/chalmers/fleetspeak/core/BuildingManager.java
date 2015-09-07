package se.chalmers.fleetspeak.core;


@FunctionalInterface
public interface BuildingManager {
	void handleCommand(String c, int roomid);
}
