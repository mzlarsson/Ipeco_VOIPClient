package se.ipeco.fleetspeak.management.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReallyDisconnectController {
	
	private static Node page;

	@FXML
	VBox root;
	
	public static void setBackPage(Node page){
		ReallyDisconnectController.page = page;
	}
	
	public void disconnect(){
		MainController.disconnect();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "login");
	}
	
	public void goBack(){
		Node backNode = page;
		if(backNode == null){
			backNode = FXUtil.getNode("home");
		}
		
		MainController.setContent(backNode);
	}
}
