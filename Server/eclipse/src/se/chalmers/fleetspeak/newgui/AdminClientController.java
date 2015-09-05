package se.chalmers.fleetspeak.newgui;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import se.chalmers.fleetspeak.newgui.core.ServerHandler;

public class AdminClientController {
	
	@FXML
	private Pane root;

	public void initialize(){
		System.out.println("In admin client controller.");
	}
	
	public void disconnect(){
		System.out.println("Disconnecting.");
		ServerHandler.disconnect();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "adminclient_login");
	}
	
}
