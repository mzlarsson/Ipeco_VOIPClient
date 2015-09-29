package se.ipeco.fleetspeak.management.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import se.ipeco.fleetspeak.management.core.Building;
import se.ipeco.fleetspeak.management.core.Building.BuildingChangeListener;
import se.ipeco.fleetspeak.management.core.Room;
import se.ipeco.fleetspeak.management.core.User;
import se.ipeco.fleetspeak.management.gui.custom.RoomPane;
import se.ipeco.fleetspeak.management.gui.custom.UserPane;
import se.ipeco.fleetspeak.management.gui.custom.UserPane.UserChangeHandler;

public class SideMenuController implements UserChangeHandler, BuildingChangeListener{

	private static SideMenuController instance;
	
	@FXML
	private SplitPane root;
	@FXML
	private VBox adminList;
	@FXML
	private VBox roomStructureBox;
	
	public SideMenuController(){
		instance = this;
	}
	
	public void initialize(){
		if(Building.hasRunningBuilding()){
			Building.getRunningBuilding().addBuildingChangeListener(this);
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
	public void lostConnection(){
		MainController.setContent(FXUtil.getNode("connectionlost"));
	}
	
	@Override
	public void removeUser(UserPane p) {
		Platform.runLater(() -> {
			adminList.getChildren().remove(p);
		});
	}
}
