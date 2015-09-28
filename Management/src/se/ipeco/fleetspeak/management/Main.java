package se.ipeco.fleetspeak.management;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import se.ipeco.fleetspeak.management.connection.ConnectionListener;
import se.ipeco.fleetspeak.management.connection.ServerHandler;
import se.ipeco.fleetspeak.management.core.Building;
import se.ipeco.fleetspeak.management.gui.FXUtil;
import se.ipeco.fleetspeak.management.util.Log;

public class Main extends Application{
	
	private static Application currentApplication;
	
	public Main(){
		super();
		currentApplication = this;
	}

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Fleetspeak server");
        primaryStage.getIcons().add(new Image(this.getClass().getClassLoader().getResourceAsStream("fleetspeak.png")));
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
        	System.out.println("Wants to close");
        	Building.terminate();
        	ServerHandler.disconnect();
        });
        
        FXUtil.switchLayout(primaryStage, "login");
//        sneakIn(primaryStage);
    }
    
    @SuppressWarnings("unused")
	private void sneakIn(Stage stage){
		ServerHandler.connect("localhost", 8867, "n", "", new ConnectionListener(){

			@Override
			public void onConnect() {
				ServerHandler server = ServerHandler.getConnectedServer();
				Building building = Building.getInstance(server.getUserID());
				server.setCommandHandler(building);
				Platform.runLater(() -> {
					FXUtil.switchLayout(stage, "main");
				});
			}

			@Override
			public void onConnectionFailure(String msg) {
				System.out.println("Failed to connect. Closing.");
			}
		});
    }

    public static Application getCurrentApplication(){
    	return currentApplication;
    }
    
    public static void main(String[] args) {
    	Log.start();
    	launch();
    }
}
