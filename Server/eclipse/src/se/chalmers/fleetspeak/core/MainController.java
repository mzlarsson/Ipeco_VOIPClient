package se.chalmers.fleetspeak.core;

import se.chalmers.fleetspeak.network.tcp.TLSConnectionHandler;

public class MainController {

	TLSConnectionHandler connectionHandler;
	Building mainBuilding;
	ClientCreator clientCreator;

	public MainController(int port){
		mainBuilding = new Building();
		clientCreator = new ClientCreator(mainBuilding);
		connectionHandler = new TLSConnectionHandler(port, clientCreator);
	}

	public void terminate(){
		connectionHandler.terminate();
		clientCreator.terminate();
	}


}
