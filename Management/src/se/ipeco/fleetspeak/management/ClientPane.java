package se.ipeco.fleetspeak.management;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ClientPane extends Pane{
	
	//Data
	private int id;
	private ObjectProperty<LoginStatus> loginStatus;
	private StringProperty usernameProperty;
	
	private ClientChangeHandler handler;

	@FXML
	private Pane root;
	@FXML
	private ImageView loginStatusView;
	@FXML
	private Label usernameLabel;
	
	public ClientPane(int id, String name){
		this.id = id;
		this.usernameProperty = new SimpleStringProperty(name);
		this.loginStatus = new SimpleObjectProperty<LoginStatus>();
		
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("clientpane.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not load clientpane: "+e.getMessage());
		}
	}
	
	public void initialize(){
		usernameLabel.textProperty().bind(usernameProperty.concat(" ["+id+"]"));
		loginStatus.addListener((ObservableValue<? extends LoginStatus> observable, LoginStatus oldValue, LoginStatus newValue) -> {
				loginStatusView.setImage(newValue.getImage());
		});
		loginStatus.set(LoginStatus.LOGGED_OUT);
	}
	
	private void initContextMenu(){
		ContextMenu menu = new ContextMenu();
		MenuItem removeItem = new MenuItem("Remove");
		final ClientPane thisPane = this;
		removeItem.setOnAction((ActionEvent event) -> {
			if(handler != null){
				handler.removeClient(thisPane);
			}
		});
		menu.getItems().add(removeItem);
		MenuItem[] status = new MenuItem[LoginStatus.values().length];
		for(int i = 0; i<status.length; i++){
			final LoginStatus currentStatus = LoginStatus.values()[i];
			status[i] = new MenuItem("Set status "+currentStatus.name());
			status[i].setOnAction((ActionEvent event) -> {
				setLoginStatus(currentStatus);
			});
			menu.getItems().add(status[i]);
		}
		
		usernameLabel.setContextMenu(menu);
	}
	
	public void setChangeHandler(ClientChangeHandler handler){
		if(this.handler == null){
			initContextMenu();
		}
		
		this.handler = handler;
	}
	
	public int getID(){
		return id;
	}
	
	public void setUsername(String username){
		usernameProperty.set(username);
	}
	
	public String getUsername(){
		return usernameProperty.get();
	}
	
	public StringProperty usernameProperty(){
		return usernameProperty;
	}
	
	public void setLoginStatus(LoginStatus status){
		loginStatus.set(status);
	}
	
	public LoginStatus getLoginStatus(){
		return loginStatus.get();
	}
	
	public ObjectProperty<LoginStatus> loginStatusProperty(){
		return loginStatus;
	}
	
	
	public enum LoginStatus{
		LOGGED_IN("loginstatus_loggedin.png"),
		IN_CALL("loginstatus_incall.png"),
		APPLICATION_INACTIVE("loginstatus_inactive.png"),
		LOGGED_OUT("loginstatus_loggedout.png");
		
		private Image image;
		private LoginStatus(String imageName){
			this.image = IconLoader.loadImage(imageName);
		}
		
		public Image getImage(){
			return image;
		}
	}
	
	public interface ClientChangeHandler{
		
		public void removeClient(ClientPane p);
		
	}
}
