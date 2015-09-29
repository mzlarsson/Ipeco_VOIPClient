package se.ipeco.fleetspeak.management.gui.custom;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import se.ipeco.fleetspeak.management.core.Room;
import se.ipeco.fleetspeak.management.core.User;
import se.ipeco.fleetspeak.management.gui.custom.UserPane.LoginStatus;

public class LargeRoomPane extends RoomPane{
	
	public LargeRoomPane(){
		super();
	}

	public LargeRoomPane(Room room) {
		super(room);
	}

	@Override
	protected void loadPane(){
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("largeroompane.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load largeroompane: "+e.getMessage());
		}
	}
	
	@Override
	protected UserPane getUserPane(User u, LoginStatus status){
		return new LargeUserPane(u, status);
	}
	
}
