package se.ipeco.fleetspeak.management.gui;

import java.util.Random;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
		
		mapView = new MapView();
		mapContainer.getChildren().add(mapView);
	}
	
	public void addRandomAdmin(){
		String[] names = {"Pelle", "Arne", "Sune", "Nano", "Volt", "Pihl", "Whoopsi Daisy", "Andreas Pettersson", "Poop"};
		int id = new Random().nextInt(names.length);
//		addAdmin(id, names[id]);
		if(Building.hasRunningBuilding()){
			Building.getRunningBuilding().printState();
		}
		
		RoomPane p = new RoomPane(new Room(id, names[id]+"s room"));
		roomStructureBox.getChildren().add(p);
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
	
	public void goToMatz(){
		mapView.moveToLocation(57.680830, 11.985843);
		mapView.addPopup(57.680830, 11.985843, "H&auml;r bor Matz!", true);
	}
	
	public void disconnect(){
		System.out.println("Disconnecting.");
		ServerHandler.disconnect();
		Building.terminate();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "login");
	}
}
