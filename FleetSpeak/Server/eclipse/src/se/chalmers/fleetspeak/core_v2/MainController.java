package se.chalmers.fleetspeak.core_v2;

import se.chalmers.fleetspeak.core.ClientCreator;
import se.chalmers.fleetspeak.core.ConnectionHandler;

public class MainController {

	ConnectionHandler connectionHandler;
	Building mainBuilding;
	ClientCreator clientCreator;

	public MainController(int port){
		mainBuilding = new Building();
		clientCreator = new ClientCreator(mainBuilding);
		connectionHandler = new ConnectionHandler(port, clientCreator);
	}

	public void terminate(){
		connectionHandler.terminate();
		clientCreator.terminate();
	}


}
