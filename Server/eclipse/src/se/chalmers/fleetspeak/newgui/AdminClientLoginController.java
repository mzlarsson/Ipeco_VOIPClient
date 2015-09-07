package se.chalmers.fleetspeak.newgui;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import se.chalmers.fleetspeak.newgui.connection.ConnectionListener;
import se.chalmers.fleetspeak.newgui.connection.ServerHandler;
import se.chalmers.fleetspeak.newgui.core.Building;

public class AdminClientLoginController implements ConnectionListener{

	@FXML
	private TextField serverIPField;
	@FXML
	private TextField serverPortField;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Button loginButton;
	@FXML
	private ImageView loadingOverlay;
	
	public void initialize(){
		serverIPField.focusedProperty().addListener((ObservableValue<? extends Boolean> value, Boolean oldProp, Boolean newProp) -> {
			if(!oldProp && newProp){
				if(serverIPField.getText().length()==0){
					serverIPField.setText(serverIPField.getPromptText());
			    	loginPropertyChanged();
				}
				serverIPField.selectAll();
			}
		});
		serverPortField.focusedProperty().addListener((ObservableValue<? extends Boolean> value, Boolean oldProp, Boolean newProp) -> {
			if(!oldProp && newProp){
				if(serverPortField.getText().length()==0){
					serverPortField.setText(serverPortField.getPromptText());
			    	loginPropertyChanged();
				}
				serverPortField.selectAll();
			}
		});
	}
	
	public void loginPropertyChanged(){
		boolean valid = true;
		try{
			Integer.parseInt(serverPortField.getText());
		}catch(NumberFormatException nfe){
			valid = false;
		}
		if(valid && serverIPField.getText().isEmpty()){
			valid = false;
		}
		if(valid && usernameField.getText().isEmpty()){
			valid = false;
		}
		
		loginButton.setDisable(!valid);
	}
	
	public void login(){
		try{
			String ip = serverIPField.getText();
			int port = Integer.parseInt(serverPortField.getText());
			String username = usernameField.getText();
			String password = passwordField.getText();
			connect(ip, port, username, password);
		}catch(NumberFormatException nfe){
			System.out.println("Invalid port. Check.");
		}
	}
	
	private void connect(String ip, int port, String username, String password){		
		showLoading(true);
		
		Runnable connector = () -> {
			ServerHandler.connect(ip, port, username, password, this);
		};
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(connector);
	}
	
	private void showLoading(boolean show){
		System.out.println((show?"Showing":"Hiding")+" loading icon");
		loadingOverlay.setVisible(show);
		loginButton.setDisable(show);
		serverIPField.setDisable(show);
		serverPortField.setDisable(show);
		usernameField.setDisable(show);
		passwordField.setDisable(show);
	}

	@Override
	public void onConnect() {
		showLoading(false);
		ServerHandler server = ServerHandler.getConnectedServer();
		Building building = Building.getInstance(server.getUsername(), server.getUserID(), server.getUserRoomID());
		server.setCommandHandler(building);
		Platform.runLater(() -> {
			FXUtil.switchLayout(getStage(), "adminclient");
		});
	}

	@Override
	public void onConnectionFailure(String msg) {
		System.out.println("Failure: "+msg);
		showLoading(false);
	}
	
	private Stage getStage(){
		return (Stage)(serverIPField.getScene().getWindow());
	}
}
