package se.ipeco.fleetspeak.management.gui;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import se.ipeco.fleetspeak.management.core.Room;
import se.ipeco.fleetspeak.management.core.User;
import se.ipeco.fleetspeak.management.gui.UserPane.LoginStatus;

public class RoomPane extends AnchorPane{
	
	private Room room;
	private BooleanProperty clientsHiddenProperty;
	
	@FXML
	private AnchorPane root;
	@FXML
	private ImageView expandIcon;
	@FXML
	private Label roomNameLabel;
	@FXML
	private VBox userContainer;

	public RoomPane(Room room){
		this.room = room;
		this.room.nbrOfUsersProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			updateUsers();
		});
		this.clientsHiddenProperty = new SimpleBooleanProperty(true);
		
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("roompane.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load roompane: "+e.getMessage());
		}
	}
	
	public void initialize(){
		roomNameLabel.textProperty().bind(room.nameProperty());
		updateUsers();
	}
	
	public Room getRoom(){
		return room;
	}
	
	private void updateUsers(){
		final List<User> users = room.getUsers();
		Platform.runLater(() -> {
			userContainer.getChildren().clear();
			for(User u : users){
				for(int i = 0; i<15; i++){
					UserPane p = new UserPane(u, LoginStatus.LOGGED_IN);
					userContainer.getChildren().add(p);
				}
			}
			System.out.println(roomNameLabel.getLayoutX()+", "+roomNameLabel.getLayoutY());
		});
	}
	
	public void toggleExpand(){
		//Only on double click
		boolean show = !clientsHiddenProperty.get();
		expandIcon.setImage(IconLoader.loadImage(show?"collapse_32x32.png":"expand_32x32.png"));
		userContainer.setManaged(show);
		userContainer.setVisible(show);
		clientsHiddenProperty.set(show);
	}
	
	public void sendToMainScreen(){
		System.out.println("To main");
	}
}
