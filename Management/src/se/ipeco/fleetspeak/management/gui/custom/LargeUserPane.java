package se.ipeco.fleetspeak.management.gui.custom;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import se.ipeco.fleetspeak.management.core.User;

public class LargeUserPane extends UserPane{

	public LargeUserPane(User user) {
		super(user);
	}
	
	public LargeUserPane(User user, LoginStatus status) {
		super(user, status);
	}
	
	@Override
	protected void loadPane(){
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("largeuserpane.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load largeclientpane: "+e.getMessage());
		}
	}

}
