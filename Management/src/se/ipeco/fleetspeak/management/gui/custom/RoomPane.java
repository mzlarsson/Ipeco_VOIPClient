package se.ipeco.fleetspeak.management.gui.custom;

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
import se.ipeco.fleetspeak.management.gui.FXUtil;
import se.ipeco.fleetspeak.management.gui.FXUtil.Function;
import se.ipeco.fleetspeak.management.gui.ImageLoader;
import se.ipeco.fleetspeak.management.gui.MainController;
import se.ipeco.fleetspeak.management.gui.RoomDisplayController;
import se.ipeco.fleetspeak.management.gui.custom.UserPane.LoginStatus;

public class RoomPane extends AnchorPane{
	
	private Room room;
	private BooleanProperty clientsHiddenProperty;
	private boolean controlsEnabled = true;
	
	@FXML
	private AnchorPane root;
	@FXML
	private ImageView expandIcon;
	@FXML
	private Label roomNameLabel;
	@FXML
	private VBox userContainer;

	public RoomPane(){
		this(null);
	}
	
	public RoomPane(Room room){
		setRoom(room);
	}
	
	public void setRoom(Room room){
		if(room != null){
			this.room = room;
			this.room.nbrOfUsersProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
				updateUsers();
			});
			this.clientsHiddenProperty = new SimpleBooleanProperty(true);
			
			loadPane();
		}
	}
	
	protected void loadPane(){
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
				UserPane p = getUserPane(u, LoginStatus.LOGGED_IN);
				userContainer.getChildren().add(p);
			}
		});
	}
	
	protected UserPane getUserPane(User u, LoginStatus status){
		return new UserPane(u, status);
	}
	
	public void toggleExpand(){
		if(controlsEnabled){
			boolean show = !clientsHiddenProperty.get();
			expandIcon.setImage(ImageLoader.loadImage(show?"collapse_32x32.png":"expand_32x32.png"));
			userContainer.setManaged(show);
			userContainer.setVisible(show);
			clientsHiddenProperty.set(show);
		}
	}
	
	public void sendToMainScreen(){
		if(controlsEnabled){
			MainController.setContent(FXUtil.getNode("largeroom", new Function() {
				@Override
				public <T> void perform(T controller) {
					if(controller instanceof RoomDisplayController){
						((RoomDisplayController)controller).setRoom(room);
					}
				}
			}));
		}
	}
	
	public void setControlsEnabled(boolean enabled){
		this.controlsEnabled = enabled;
	}
}
