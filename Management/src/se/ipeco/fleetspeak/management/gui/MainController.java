package se.ipeco.fleetspeak.management.gui;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;

public class MainController{
	
	private static MainController instance;
	private static Logger logger = Logger.getLogger("Debug");
	
	@FXML
	private AnchorPane root;
	@FXML
	private ToolBar toolbar;
	@FXML
	private AnchorPane menuPane;
	@FXML
	private AnchorPane contentRoot;

	public MainController(){
		instance = this;
	}
	
	public void initialize(){
		//Init menubar
		toggleFedora();
		
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
	
	public void gotoFedora(){
		ImageView view = new ImageView();
		Image im = ImageLoader.loadImage("fedora_animation.gif");
		if(im != null){
			view.setImage(im);
			view.setPreserveRatio(true);
		}
		
		BorderPane pane = new BorderPane();
		AnchorPane.setBottomAnchor(pane, 0d);
		AnchorPane.setTopAnchor(pane, 0d);
		AnchorPane.setRightAnchor(pane, 0d);
		AnchorPane.setLeftAnchor(pane, 0d);
		pane.setCenter(view);
		
		setContent(pane);
	}
	
	public void toggleMenubar(){
		toolbar.setVisible(!toolbar.isVisible());
		toolbar.setManaged(!toolbar.isManaged());
	}
	
	public void toggleFedora(){
		Node fedoraNode = null;
		for(Node n : toolbar.getItems()){
			if(n.getId() != null && n.getId().equals("fedoraButton")){
				fedoraNode = n;
				break;
			}
		}
		
		if(fedoraNode != null){
			fedoraNode.setVisible(!fedoraNode.isVisible());
			fedoraNode.setManaged(!fedoraNode.isManaged());
		}
	}
	
	public void showAbout(){
		FXUtil.showModalWindow(root.getScene().getWindow(), (Parent)FXUtil.getNode("about"),
								"About Fleetspeak", ImageLoader.loadImage("fleetspeak.png"));
	}
	
	public void logout(){
		disconnect();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "login");
	}
	
	public void exit(){
		disconnect();
		System.exit(0);
	}
	
	public static void setContent(Node contentNode){
		Platform.runLater(() -> {
			instance.contentRoot.getChildren().clear();
			if(contentNode != null){
				instance.contentRoot.getChildren().add(contentNode);
			}
		});
	}
	
	public static void disconnect(){
		logger.info("Disconnecting.");
		Building.terminate();
		ServerHandler.disconnect();
	}
}
