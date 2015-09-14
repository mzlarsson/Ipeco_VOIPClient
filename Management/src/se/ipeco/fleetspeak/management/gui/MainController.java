package se.ipeco.fleetspeak.management.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

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
		setContent(FXUtil.getNode("tmpcontent"));
	}
	
	public void addAdmin(int id, String name){
		SideMenuController.getInstance().addAdmin(id, name);
	}
	
	public void removeAdmin(int id){
		SideMenuController.getInstance().removeAdmin(id);
	}
	
	
	public void buttonFieldToggled(){
		SideMenuController.getInstance().toggleButtonView();
	}
	
	public static void setContent(Node contentNode){
		instance.contentRoot.getChildren().clear();
		if(contentNode != null){
			instance.contentRoot.getChildren().add(contentNode);
		}
	}
}
