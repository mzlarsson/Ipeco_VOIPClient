package se.ipeco.fleetspeak.management.gui;

import javafx.fxml.FXML;
import se.ipeco.fleetspeak.management.core.Room;
import se.ipeco.fleetspeak.management.gui.custom.LargeRoomPane;

public class RoomDisplayController{

	@FXML
	private LargeRoomPane roomPane;
	
	public void initialize(){
	}
	
	public void setRoom(Room room){
		roomPane.setRoom(room);
		roomPane.setControlsEnabled(false);
	}
}
