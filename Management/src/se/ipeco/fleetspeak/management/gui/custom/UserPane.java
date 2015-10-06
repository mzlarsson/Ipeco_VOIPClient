package se.ipeco.fleetspeak.management.gui.custom;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.ipeco.fleetspeak.management.core.User;
import se.ipeco.fleetspeak.management.gui.ImageLoader;

public class UserPane extends AnchorPane{
	
	//Data
	private User user;
	private ObjectProperty<LoginStatus> loginStatus;
	private LoginStatus initialLoginStatus;
	
	private UserChangeHandler handler;

	@FXML
	private AnchorPane root;
	@FXML
	private ImageView loginStatusView;
	@FXML
	private Label usernameLabel;
	
	public UserPane(User user){
		this(user, LoginStatus.LOGGED_IN);
	}
	
	public UserPane(User user, LoginStatus status){
		this.user = user;
		this.initialLoginStatus = (status==null?LoginStatus.LOGGED_IN:status);
		this.loginStatus = new SimpleObjectProperty<LoginStatus>();
		
		loadPane();
	}
	
	protected void loadPane(){
		FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource("userpane.fxml"));
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
		usernameLabel.textProperty().bind(Bindings.concat(user.usernameProperty(), " [", user.idProperty(), "]"));
		loginStatus.addListener((ObservableValue<? extends LoginStatus> observable, LoginStatus oldValue, LoginStatus newValue) -> {
			if(newValue != null){
				loginStatusView.setImage(newValue.getImage());
			}
		});
		
		loginStatus.set(initialLoginStatus);
	}
	
	private void initContextMenu(){
		ContextMenu menu = new ContextMenu();
		MenuItem removeItem = new MenuItem("Remove");
		final UserPane thisPane = this;
		removeItem.setOnAction((ActionEvent event) -> {
			if(handler != null){
				handler.removeUser(thisPane);
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
	
	public void setChangeHandler(UserChangeHandler handler){
		if(this.handler == null){
			initContextMenu();
		}
		
		this.handler = handler;
	}
	
	public User getUser(){
		return user;
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
			this.image = ImageLoader.loadImage(imageName);
		}
		
		public Image getImage(){
			return image;
		}
	}
	
	public interface UserChangeHandler{
		
		public void removeUser(UserPane p);
		
	}
}
