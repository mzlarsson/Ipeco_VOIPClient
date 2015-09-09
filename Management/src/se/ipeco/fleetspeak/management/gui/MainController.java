package se.ipeco.fleetspeak.management.gui;

import java.util.Arrays;
import java.util.Random;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;
import se.ipeco.fleetspeak.management.gui.ClientPane.ClientChangeHandler;

public class MainController implements ClientChangeHandler{
	
	@FXML
	private AnchorPane root;
	@FXML
	private VBox adminList;

	public void initialize(){
		System.out.println("In admin client controller.");
		System.out.println(Arrays.toString(adminList.getStyleClass().toArray(new String[0])));
	}
	
	public void addRandomAdmin(){
		String[] names = {"Pelle", "Arne", "Sune", "Nano", "Volt", "Pihl", "Whoopsi Daisy", "Andreas Pettersson", "Poop"};
		int id = new Random().nextInt(names.length);
		addAdmin(id, names[id]);
	}
	
	public void addAdmin(int id, String name){
		Platform.runLater(() -> {
			ClientPane p = new ClientPane(id, name);
			p.setChangeHandler(this);
			adminList.getChildren().add(p);
		});
	}
	
	public void removeAdmin(int id){
		ObservableList<Node> admins = adminList.getChildren();
		for(int i = 0; i<admins.size(); i++){
			if(admins.get(i) instanceof ClientPane){
				if(((ClientPane)admins.get(i)).getID()==id){
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
	public void removeClient(ClientPane p) {
		Platform.runLater(() -> {
			adminList.getChildren().remove(p);
		});
	}
	
	public void disconnect(){
		System.out.println("Disconnecting.");
		ServerHandler.disconnect();
		Building.terminate();
		FXUtil.switchLayout((Stage)root.getScene().getWindow(), "login");
	}
}
