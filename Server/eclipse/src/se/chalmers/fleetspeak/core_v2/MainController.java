package se.chalmers.fleetspeak.core_v2;

import se.chalmers.fleetspeak.core.ClientCreator;
import se.chalmers.fleetspeak.network.TLSConnectionHandler;

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
