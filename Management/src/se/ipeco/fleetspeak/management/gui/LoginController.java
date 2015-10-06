package se.ipeco.fleetspeak.management.gui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import se.ipeco.fleetspeak.management.connection.ConnectionListener;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;

public class LoginController implements ConnectionListener{

	private static boolean firstLaunch = true;
	
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
	@FXML
	private VBox contentBox;
	@FXML
	private CheckBox autoLoginCheckbox;
	
	public void initialize(){		
		serverIPField.focusedProperty().addListener((ObservableValue<? extends Boolean> value, Boolean oldProp, Boolean newProp) -> {
			if(!oldProp && newProp){
				if(serverIPField.getText().length()==0){
					serverIPField.setText(serverIPField.getPromptText());
//					serverIPField.setText("localhost");
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

		//Auto login functionality
		tryToAutoLogin();
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
	
	private void tryToAutoLogin(){
		if(hasLoginSetting()){
			LoginSetting s = loadLoginSetting();
			serverIPField.setText(s.ip);
			serverPortField.setText(s.port+"");
			usernameField.setText(s.username);
			passwordField.setText(s.password);
			autoLoginCheckbox.setSelected(s.autologin);
			loginPropertyChanged();
			
			if(firstLaunch){
				firstLaunch = false;
				if(s.autologin){
					login();
				}
			}
		}
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
		
		saveLoginSetting(new LoginSetting(ip, port, username, password, false, autoLoginCheckbox.isSelected()));
		
		Runnable connector = () -> {
			ServerHandler.connect(ip, port, username, password, this);
		};
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(connector);
	}
	
	private void showLoading(boolean show){
		loadingOverlay.setVisible(show);
		loginButton.setDisable(show);
		serverIPField.setDisable(show);
		serverPortField.setDisable(show);
		usernameField.setDisable(show);
		passwordField.setDisable(show);
	}
	
	private void showError(String msg){
		Platform.runLater(() -> {
			//Reuse previous error msg?
			if(contentBox.getChildren().size()>0 && contentBox.getChildren().get(0).getStyleClass().contains("errorMsg")){
				((Label)contentBox.getChildren().get(0)).setText(msg);
			}else{
				Label label = new Label(msg);
				label.getStyleClass().add("errorMsg");
				label.setWrapText(true);
				label.setMaxWidth(contentBox.getWidth());
				contentBox.getChildren().add(0, label);
			}
		});
	}

	@Override
	public void onConnect() {
		showLoading(false);
		ServerHandler server = ServerHandler.getConnectedServer();
		Building building = Building.getInstance(server.getUserID());
		server.setCommandHandler(building);
		Platform.runLater(() -> {
			FXUtil.switchLayout(getStage(), "main", true);
		});
	}

	@Override
	public void onConnectionFailure(String msg) {
		showError(msg);
		showLoading(false);
	}
	
	private Stage getStage(){
		return (Stage)(serverIPField.getScene().getWindow());
	}
	
	
	private static void saveLoginSetting(LoginSetting s){
		File saveFile = new File("savedata/autologin.dat");
		if(s != null){
			s.encodePassword();
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(saveFile));
				StringBuffer b = new StringBuffer();
				b.append(s.ip).append("\n");
				b.append(s.port).append("\n");
				b.append(s.username).append("\n");
				b.append(s.password).append("\n");
				b.append(s.autologin);
				out.write(b.toString().getBytes());
				out.close();
			} catch (FileNotFoundException e) {
				System.out.println("Could not find autologin save file");
			} catch (IOException e) {
				System.out.println("Got error while saving autologins: "+e.getMessage());
			}
		}else{
			saveFile.delete(); 
		}
	}
	
	private static boolean hasLoginSetting(){
		return new File("savedata/autologin.dat").exists();
	}
	
	private static LoginSetting loadLoginSetting(){
		File dataFile = new File("savedata/autologin.dat");
		if(dataFile.exists()){
			Scanner sc = null;
			try {
				sc = new Scanner(dataFile);
				LoginSetting s = new LoginSetting(sc.nextLine(), Integer.parseInt(sc.nextLine()), sc.nextLine(), sc.nextLine(), true, sc.nextLine().equals("true"));
				s.decodePassword();
				sc.close();
				return s;
			} catch (FileNotFoundException fnfe) {
				System.out.println("Could not find data file. (Something went strangely wrong)");
			} catch (NumberFormatException nfe){
				System.out.println("Invalid file (port not a number). Removing it.");
				sc.close();
				dataFile.delete();
			} catch (InputMismatchException ime){
				System.out.println("Invalid file. Removing it.");
				sc.close();
				dataFile.delete();
			}
		}
		
		return null;
	}
	
	private static class LoginSetting{
		private String ip;
		private int port;
		private String username;
		private String password;
		private boolean encoded;
		private boolean autologin;
		private LoginSetting(String ip, int port, String username, String password, boolean encoded, boolean autologin){
			this.ip = ip;
			this.port = port;
			this.username = username;
			this.password = password;
			this.encoded = encoded;
			this.autologin = autologin;
		}
		
		private void encodePassword(){
			if(!encoded){
				StringBuffer b = new StringBuffer();
				Random r = new Random();
				for(int i = 0; i<12; i++){
					b.append((char)(r.nextInt(89)+33));
				}
				
				for(char c : password.toCharArray()){
					b.append((char)(c+2));
				}
	
				for(int i = 0; i<7; i++){
					b.append((char)(r.nextInt(89)+33));
				}
				
				password = b.toString();
				encoded = true;
			}
		}
		
		private void decodePassword(){
			if(encoded){
				String pw = password.substring(12, password.length()-7);
				StringBuffer b = new StringBuffer();
				for(char c : pw.toCharArray()){
					b.append((char)(c-2));
				}
				
				password = b.toString();
				encoded = false;
			}
		}
	}
}
