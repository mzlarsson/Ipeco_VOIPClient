package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.sound.Mixer;
import se.chalmers.fleetspeak.util.Command;

public class AudioRoom implements IRoom {

	private Room room;
	private Mixer mixer;

	public AudioRoom(String name, BuildingManager buildingManager, boolean permanent){
		room = new Room(name, buildingManager, permanent);
		//TODO init mixer
	}


	@Override
	public void addClient(Client client) {
		room.addClient(client);
		//TODO add mixer stuff
	}

	@Override
	public Client removeClient(int clientid) {
		Client c = room.removeClient(clientid);
		//TODO do mixer stuff
		return c;
	}

	@Override
	public boolean canDelete() {

		return room.canDelete();
	}

	@Override
	public Integer getId() {
		return room.getId();
	}

	@Override
	public String getName() {
		return room.getName();
	}

	@Override
	public void setName(String name) {
		room.setName(name);
	}

	@Override
	public void postUpdate(Command c) {
		room.postUpdate(c);
	}

	@Override
	public void sync(Client c) {
		room.sync(c);
	}

}
