package se.ipeco.fleetspeak.management.gui;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;
import se.ipeco.fleetspeak.management.core.Building.BuildingChangeListener;
import se.ipeco.fleetspeak.management.core.Room;
import se.ipeco.fleetspeak.management.core.User;
import se.ipeco.fleetspeak.management.gui.UserPane.UserChangeHandler;

public class MainController implements UserChangeHandler, BuildingChangeListener{
	
	@FXML
	private AnchorPane root;
	@FXML
	private VBox adminList;
	@FXML
	private VBox roomStructureBox;
	@FXML
	private Pane mapContainer;
	
	private MapView mapView;

	public void initialize(){
		System.out.println("In admin client controller.");
		if(Building.hasRunningBuilding()){
			Building.getRunningBuilding().setBuildingChangeListener(this);
		}
		
		mapView = new MapView(650, 450);
		mapContainer.getChildren().add(mapView);
		mapView.loadedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean wasLoaded, Boolean isLoaded) -> {
			if(isLoaded){
				mapView.addPopup(57.687325, 11.978694, "Idegr6");
			}
		});
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
	
	public void clearMap(){
		mapView.clearMap();
	}
	
	public void goToMatz(){
		mapView.moveToLocation(57.680830, 11.985843);
		mapView.addCircle(57.680830, 11.985843, 20, Color.RED, "H&auml;r bor Matz!");
	}
	
	public void disconnect(){
		System.out.println("Disconnecting.");
		ServerHandler.disconnect();
		Building.terminate();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "login");
	}
}
