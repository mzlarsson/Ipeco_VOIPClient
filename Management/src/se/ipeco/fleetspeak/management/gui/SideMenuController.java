package se.ipeco.fleetspeak.management.gui;

import se.ipeco.fleetspeak.management.core.Building;
import se.ipeco.fleetspeak.management.core.Room;
import se.ipeco.fleetspeak.management.core.User;
import se.ipeco.fleetspeak.management.core.Building.BuildingChangeListener;
import se.ipeco.fleetspeak.management.gui.UserPane.UserChangeHandler;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SideMenuController implements UserChangeHandler, BuildingChangeListener{

	private static SideMenuController instance;
	
	@FXML
	private SplitPane root;
	@FXML
	private AnchorPane buttonPane;
	@FXML
	private VBox adminList;
	@FXML
	private VBox roomStructureBox;
	
	public SideMenuController(){
		instance = this;
	}
	
	public void initialize(){
		if(Building.hasRunningBuilding()){
			Building.getRunningBuilding().setBuildingChangeListener(this);
		}
	}
	
	public static SideMenuController getInstance(){
		return instance;
	}
	
	public void addAdmin(int id, String name){
		Platform.runLater(() -> {
			UserPane p = new UserPane(new User(id, name));
			p.setChangeHandler(this);
			adminList.getChildren().add(p);
		});
	}
	
	public void removeAdmin(int id){
		ObservableList<Node> admins = adminList.getChildren();
		for(int i = 0; i<admins.size(); i++){
			if(admins.get(i) instanceof UserPane){
				if(((UserPane)admins.get(i)).getUser().getID()==id){
					final int index = i;
					Platform.runLater(() -> {
						adminList.getChildren().remove(index);
					});
					return;
				}
			}
		}
	}

	@Override
	public void addedRoom(Room r) {
		Platform.runLater(() -> {
			RoomPane p = new RoomPane(r);
			roomStructureBox.getChildren().add(p);
		});
	}

	@Override
	public void removedRoom(Room r) {
		Platform.runLater(() -> {
			Node roomPane = null;
			for(Node n : roomStructureBox.getChildren()){
				if(n instanceof RoomPane && ((RoomPane)n).getRoom().equals(r)){
					roomPane = n;
					break;
				}
			}
			
			if(roomPane != null){
				roomStructureBox.getChildren().remove(roomPane);
			}
		});
	}
	
	@Override
	public void removeUser(UserPane p) {
		Platform.runLater(() -> {
			adminList.getChildren().remove(p);
		});
	}
	
	public void toggleButtonView(){
		System.out.println("toggeling button field");
		boolean visible = !buttonPane.isVisible();
		buttonPane.setVisible(visible);
//		buttonPane.setManaged(visible);
		if(visible){
			root.getItems().add(0, buttonPane);
			root.getDividers().get(0).setPosition(0.2);
		}else{
			root.getItems().remove(buttonPane);
		}
	}
}
