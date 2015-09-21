package se.ipeco.fleetspeak.management.gui;

import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainController{
	
	private static MainController instance;
	
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane menuPane;
	@FXML
	private AnchorPane contentRoot;

	public MainController(){
		instance = this;
	}
	
	public void initialize(){
		System.out.println("In admin client controller.");
		
		//Init menu
		menuPane.getChildren().add(FXUtil.getNode("sidemenu"));
		
		//Init content
		gotoHome();
	}
	
	public void addAdmin(int id, String name){
		SideMenuController.getInstance().addAdmin(id, name);
	}
	
	public void removeAdmin(int id){
		SideMenuController.getInstance().removeAdmin(id);
	}
	
	public void gotoHome(){
		setContent(FXUtil.getNode("home"));
	}
	
	public void gotoSpeakMode(){
		setContent(null);
	}
	
	public void close(){
		//Save current content as memory if user does not connect.
		Node currentContent = null;
		if(contentRoot.getChildren().size()>0){
			currentContent = contentRoot.getChildren().get(0);
		}
		ReallyDisconnectController.setBackPage(currentContent);
		
		//Ask if he/she really wants to connect
		setContent(FXUtil.getNode("reallydisconnect"));
	}
	
	public static void setContent(Node contentNode){
		Platform.runLater(() -> {
			instance.contentRoot.getChildren().clear();
			if(contentNode != null){
				instance.contentRoot.getChildren().add(contentNode);
			}
		});
	}
	
	public static void showDisconnectScreen(){
		if(instance != null){
			instance.close();
		}
	}
	
	public static void disconnect(){
		System.out.println("Disconnecting.");
		ServerHandler.disconnect();
		Building.terminate();
		FXUtil.switchLayout((Stage)instance.root.getScene().getWindow(), "login");
	}
}
