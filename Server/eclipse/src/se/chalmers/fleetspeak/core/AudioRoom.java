package se.chalmers.fleetspeak.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.sound.Mixer;
import se.chalmers.fleetspeak.sound.MixerFactory;
import se.chalmers.fleetspeak.util.Command;

public class AudioRoom implements IRoom {

	private Room room;
	private Mixer mixer;
	private Thread mixerThread;
	private Logger logger;

	public AudioRoom(String name, BuildingManager buildingManager, boolean permanent){
		logger = Logger.getLogger("Debug");
		room = new Room(name, buildingManager, permanent);
		mixer = MixerFactory.getDefaultMixer();
		mixerThread = new Thread(mixer, "Mixer "+name);
		mixerThread.start();
	}


	@Override
	public void addClient(Client client) {
		room.addClient(client);
		mixer.addStream(client.getAudioStream(), client.getOutputBuffer());
	}

	@Override
	public Client removeClient(int clientid) {
		Client c = room.removeClient(clientid);
		if (c!=null) {
			mixer.removeStream(c.getAudioStream());
		} else {
			logger.log(Level.SEVERE, "Client was null when removedClient was called");
		}
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
	
	@Override
	public void terminate(){
		mixer.close();
		room.terminate();
	}

}
