package se.chalmers.fleetspeak.core;

import java.util.logging.Level;
import java.util.logging.Logger;

import se.chalmers.fleetspeak.sound.Mixer;
import se.chalmers.fleetspeak.sound.MixerFactory;

public class AudioRoom implements IRoom {

	private Room room;
	private Mixer mixer;
	private Thread mixerThread;
	private Logger logger;

	private int nbrOfClients = 0;
	private String roomName;

	public AudioRoom(String name, BuildingManager buildingManager, boolean permanent){
		logger = Logger.getLogger("Debug");
		room = new Room(name, buildingManager, permanent);
		this.roomName = name;
	}

	private void startMixer(){
		mixer = MixerFactory.getDefaultMixer();
		mixerThread = new Thread(mixer, "Mixer "+roomName);
		mixerThread.start();
	}

	@Override
	public void addClient(Client client) {
		room.addClient(client);
		if(mixer == null){
			startMixer();
		}
		mixer.addStream(client.getAudioStream(), client.getOutputBuffer());
		nbrOfClients++;
	}

	@Override
	public Client removeClient(int clientid) {
		Client c = room.removeClient(clientid);
		if (c!=null) {
			mixer.removeStream(c.getAudioStream());
			nbrOfClients--;
			if(nbrOfClients==0 && mixer != null){
				mixer.close();
				mixer = null;
			}
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
	public void postUpdate(String c) {
		room.postUpdate(c);
	}

	@Override
	public Client findClient(int id) {
		return room.findClient(id);
	}
	
	@Override
	public void sync(Client c) {
		room.sync(c);
	}

	@Override
	public void terminate(){
		if (mixer!=null) {
			mixer.close();
		}
		room.terminate();
	}

	@Override
	public void sendCommandToClient(int clientid, String message) {
		room.sendCommandToClient(clientid, message);

	}

}
